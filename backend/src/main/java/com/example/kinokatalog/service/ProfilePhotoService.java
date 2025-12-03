package com.example.kinokatalog.service;

import io.minio.GetObjectArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import net.coobird.thumbnailator.Thumbnails;

import jakarta.annotation.PostConstruct;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfilePhotoService {

    private final MinioClient minioClient;
    private final Tika tika = new Tika();

    @Value("${app.minio.incoming-bucket:profiles-incoming}")
    private String incomingBucket;

    @Value("${app.minio.final-bucket:profiles-final}")
    private String finalBucket;

    @Value("${app.minio.max-bytes:5242880}") // 5MB default
    private long maxBytes;

    @Value("${app.clamav.host:clamav}")
    private String clamavHost;

    @Value("${app.clamav.port:3310}")
    private int clamavPort;

    @PostConstruct
    public void ensureBuckets() throws Exception {
        if (!minioClient.bucketExists(io.minio.BucketExistsArgs.builder().bucket(incomingBucket).build())) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(incomingBucket).build());
        }
        if (!minioClient.bucketExists(io.minio.BucketExistsArgs.builder().bucket(finalBucket).build())) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(finalBucket).build());
        }
    }

    /**
     * Create presigned PUT URL into the incoming (quarantine) bucket.
     * Server chooses object key to avoid trusting client filenames.
     */
    public Map<String, String> presignUpload(Integer userId, String filename, String contentType, Long size) throws Exception {
        if (size != null && size > maxBytes) throw new IllegalArgumentException("File too large");

        String ext = "";
        if (filename != null && filename.contains(".")) ext = filename.substring(filename.lastIndexOf('.'));
        String objectKey = String.format("profiles/%d/%s%s", userId, UUID.randomUUID(), ext);

        String uploadUrl = minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .method(Method.PUT)
                        .bucket(incomingBucket)
                        .object(objectKey)
                        .expiry((int) Duration.ofMinutes(15).getSeconds())
                        .build()
        );

        return Map.of("uploadUrl", uploadUrl, "objectKey", objectKey);
    }

    /**
     * Confirm uploaded object: validate magic bytes (Tika), run clamd INSTREAM scan, re-encode/sanitize and promote to final bucket.
     * Returns final object key (to be stored in DB).
     */
    public String confirmAndPromote(Integer userId, String objectKey) throws Exception {
        StatObjectResponse stat = minioClient.statObject(StatObjectArgs.builder()
                .bucket(incomingBucket).object(objectKey).build());
        if (stat.size() > maxBytes) {
            minioClient.removeObject(RemoveObjectArgs.builder().bucket(incomingBucket).object(objectKey).build());
            throw new IllegalArgumentException("File too large");
        }

        File tmpIn = Files.createTempFile("upload-", ".tmp").toFile();
        try (InputStream is = minioClient.getObject(GetObjectArgs.builder().bucket(incomingBucket).object(objectKey).build());
             OutputStream os = new FileOutputStream(tmpIn)) {
            is.transferTo(os);
        }

        try {
            String detected = tika.detect(tmpIn);
            if (!("image/jpeg".equals(detected) || "image/png".equals(detected) || "image/webp".equals(detected))) {
                minioClient.removeObject(RemoveObjectArgs.builder().bucket(incomingBucket).object(objectKey).build());
                throw new IllegalArgumentException("Invalid file type: " + detected);
            }

            // clamd INSTREAM scan (TCP)
            boolean clean = scanWithClamd(tmpIn);
            if (!clean) {
                minioClient.removeObject(RemoveObjectArgs.builder().bucket(incomingBucket).object(objectKey).build());
                throw new IllegalArgumentException("File failed virus scan (infected)");
            }

            // re-encode and strip metadata -> JPEG
            File tmpOut = Files.createTempFile("sanitized-", ".jpg").toFile();
            try {
                BufferedImage img = ImageIO.read(tmpIn);
                if (img == null) throw new IllegalArgumentException("Cannot decode image");

                Thumbnails.of(img)
                        .size(2000, 2000)
                        .outputFormat("jpg")
                        .outputQuality(0.85)
                        .toFile(tmpOut);

                String finalKey = String.format("profiles/%d/%s.jpg", userId, UUID.randomUUID());
                try (InputStream sanitizedStream = new FileInputStream(tmpOut)) {
                    minioClient.putObject(PutObjectArgs.builder()
                            .bucket(finalBucket)
                            .object(finalKey)
                            .stream(sanitizedStream, tmpOut.length(), -1)
                            .contentType("image/jpeg")
                            .build());
                }

                // remove incoming object
                minioClient.removeObject(RemoveObjectArgs.builder().bucket(incomingBucket).object(objectKey).build());

                return finalKey;
            } finally {
                tmpOut.delete();
            }
        } finally {
            tmpIn.delete();
        }
    }

    /**
     * Support direct multipart upload (server accepts file, stores to incoming, then validates/promotes).
     */
    public String upload(MultipartFile file, Long userId) throws Exception {
        if (file.getSize() > maxBytes) throw new IllegalArgumentException("File too large");
        String tempKey = String.format("profiles/%d/%s.tmp", userId, UUID.randomUUID());
        try (InputStream is = file.getInputStream()) {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(incomingBucket)
                    .object(tempKey)
                    .stream(is, file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build());
        }
        return confirmAndPromote(userId.intValue(), tempKey);
    }

    /**
     * Presigned GET for final object (short expiry).
     */
    public String getPresignedUrl(String objectKey) throws Exception {
        return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .method(Method.GET)
                        .bucket(finalBucket)
                        .object(objectKey)
                        .expiry((int) Duration.ofMinutes(10).getSeconds())
                        .build()
        );
    }

    /**
     * Minimal clamd INSTREAM implementation over TCP (no external dependency).
     * Sends file in chunks and reads the single-line response.
     */
    private boolean scanWithClamd(File file) throws IOException {
        try (Socket socket = new Socket(clamavHost, clamavPort);
             OutputStream out = socket.getOutputStream();
             InputStream in = socket.getInputStream();
             FileChannel fc = FileChannel.open(file.toPath(), StandardOpenOption.READ)) {

            // send INSTREAM command
            out.write("INSTREAM\n".getBytes());
            out.flush();

            ByteBuffer buf = ByteBuffer.allocate(64 * 1024);
            while (fc.position() < fc.size()) {
                buf.clear();
                int read = fc.read(buf);
                if (read <= 0) break;
                buf.flip();
                byte[] chunk = new byte[buf.remaining()];
                buf.get(chunk);

                // write 4-byte big-endian length, then chunk
                out.write(ByteBuffer.allocate(4).putInt(chunk.length).array());
                out.write(chunk);
                out.flush();
            }

            // zero-length chunk to finish
            out.write(new byte[]{0, 0, 0, 0});
            out.flush();

            BufferedReader rdr = new BufferedReader(new InputStreamReader(in));
            String response = rdr.readLine();
            if (response == null) throw new IOException("No response from clamd");

            if (response.contains("OK")) return true;
            if (response.contains("FOUND")) return false;
            throw new IOException("clamd error: " + response);
        }
    }
}