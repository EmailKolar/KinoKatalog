import React, { useEffect, useRef, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import {
  Box,
  Heading,
  Spinner,
  Text,
  Stack,
  Avatar,
  Button,
  Input,
  HStack,
  Alert,
  AlertIcon,
} from "@chakra-ui/react";
import { useAuth } from "../services/auth";
import { axiosInstance } from "../services/api-client"; // IMPORTANT

const allowedTypes = ["image/jpeg", "image/png", "image/webp"];
const maxSize = 5 * 1024 * 1024; // 5MB

const UserDetailPage: React.FC = () => {
  const { id } = useParams();
  const auth = useAuth();
  const navigate = useNavigate();
  const fileRef = useRef<HTMLInputElement | null>(null);

  const [loading, setLoading] = useState(true);
  const [previewUrl, setPreviewUrl] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [uploading, setUploading] = useState(false);
  const [success, setSuccess] = useState(false);

  useEffect(() => {
    (async () => {
      try {
        await auth.ensureUserLoaded();
        if (String(auth.user?.id) !== String(id)) {
          navigate("/", { replace: true });
          return;
        }
      } catch {
        auth.logout();
        navigate("/", { replace: true });
        return;
      } finally {
        setLoading(false);
      }
    })();
  }, [id]); // eslint-disable-line

  if (loading) return <Spinner />;

  const user = auth.user!;
  const onChooseFile = () => fileRef.current?.click();

  const validateImage = (file: File): Promise<boolean> =>
    new Promise((resolve) => {
      setError(null);
      setSuccess(false);

      if (!allowedTypes.includes(file.type)) {
        setError("Only JPEG, PNG, and WebP images are allowed.");
        return resolve(false);
      }
      if (file.size > maxSize) {
        setError("Image is too large (max 5MB).");
        return resolve(false);
      }

      const img = new Image();
      img.src = URL.createObjectURL(file);
      img.onload = () => resolve(true);
      img.onerror = () => {
        setError("File is not a valid image.");
        resolve(false);
      };
    });

  const onFileChange: React.ChangeEventHandler<HTMLInputElement> = async (e) => {
    const file = e.target.files?.[0];
    if (!file) return;

    const isValid = await validateImage(file);
    if (!isValid) return;

    setPreviewUrl(URL.createObjectURL(file));

    // upload right away or show a separate "Upload" button
    await uploadToBackend(file);
  };

  const uploadToBackend = async (file: File) => {
    setUploading(true);
    setError(null);
    setSuccess(false);

    try {
      const formData = new FormData();
      formData.append("file", file);

      await axiosInstance.post("/users/upload", formData, {
        headers: { "Content-Type": "multipart/form-data" },
      });

      setSuccess(true);
    } catch (err: any) {
      setError(err.response?.data || "Upload failed");
    } finally {
      setUploading(false);
    }
  };

  return (
    <Box p={6} maxW="720px" mx="auto">
      <HStack justifyContent="space-between" mb={6}>
        <Heading size="lg">Your profile</Heading>
        <Text color="gray.600">
          Registered {new Date(user.createdAt).toLocaleDateString()}
        </Text>
      </HStack>

      <Stack direction={["column", "row"]} spacing={6} alignItems="center">
        <Box textAlign="center">
          <Avatar
            size="2xl"
            name={user.fullName || user.username}
            src={previewUrl ?? undefined}
            mb={3}
          />

          <Input
            ref={fileRef}
            type="file"
            accept="image/*"
            display="none"
            onChange={onFileChange}
          />

          <Button onClick={onChooseFile} size="sm" variant="outline" mb={2}>
            Change photo
          </Button>

          {uploading && <Text fontSize="xs">Uploading…</Text>}
          {error && (
            <Alert status="error" mt={2}>
              <AlertIcon />
              {error}
            </Alert>
          )}
          {success && (
            <Alert status="success" mt={2}>
              <AlertIcon />
              Profile picture updated!
            </Alert>
          )}
        </Box>

        <Box flex="1">
          <Heading size="md" mb={2}>
            {user.fullName || user.username}
          </Heading>
          <Text mb={1}>
            <strong>Username:</strong> {user.username}
          </Text>
          <Text mb={1}>
            <strong>Email:</strong> {user.email}
          </Text>
          <Text mb={1}>
            <strong>Role:</strong> {user.role}
          </Text>
          <Text mb={1}>
            <strong>ID:</strong> {user.id}
          </Text>
        </Box>
      </Stack>
    </Box>
  );
};

export default UserDetailPage;
