import { useAuth } from "../../services/auth";
import { axiosInstance } from "../../services/api-client";

export function useChangeProfilePicture() {
  const auth = useAuth();

  const upload = async (file: File) => {
    console.log("Starting upload process...");
    if (!auth.user) throw new Error("User not logged in");

    const userId = auth.user.id;

    // Request presigned PUT URL - server chooses objectKey
    const presignRes = await axiosInstance.post(`/users/${userId}/profile-image/presign`, {
      filename: file.name,
      contentType: file.type,
      size: file.size,
    });
    const { uploadUrl, objectKey } = presignRes.data;

    // Upload file directly to MinIO using presigned URL (must set Content-Type)
    const putRes = await fetch(uploadUrl, {
      method: "PUT",
      body: file,
      headers: { "Content-Type": file.type },
    });
    if (!putRes.ok) throw new Error("Failed to upload to MinIO");

    // Confirm upload so backend validates/records the objectKey
    const confirmRes = await axiosInstance.post(`/users/${userId}/profile-image/confirm`, { objectKey });
    const updatedUser = confirmRes.data;

    // refresh auth user
    await auth.refresh();
    return updatedUser;
  };

  return { upload };
}