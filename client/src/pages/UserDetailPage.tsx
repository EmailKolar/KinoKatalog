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
} from "@chakra-ui/react";
import { useAuth } from "../services/auth";
import { useChangeProfilePicture } from "../domain/user/UseChangeProfilePicture";

const UserDetailPage: React.FC = () => {
  const { id } = useParams();
  const auth = useAuth();
  const navigate = useNavigate();
  const fileRef = useRef<HTMLInputElement | null>(null);
  const [loading, setLoading] = useState(true);
  const [previewUrl, setPreviewUrl] = useState<string | null>(null);

  useEffect(() => {
    (async () => {
      try {
        // load current user only when needed
        await auth.ensureUserLoaded();
        // if the loaded user id doesn't match route id, redirect
        if (String(auth.user?.id) !== String(id)) {
          navigate("/", { replace: true });
          return;
        }
      } catch {
        // token invalid or fetch failed -> sign out and redirect
        auth.logout();
        navigate("/", { replace: true });
        return;
      } finally {
        setLoading(false);
      }
    })();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [id]);

  if (loading) return <Spinner />;

  const user = auth.user!;
  const onChooseFile = () => fileRef.current?.click();

  const changeProfile = useChangeProfilePicture();

const onFileChange: React.ChangeEventHandler<HTMLInputElement> = async (e) => {
  const f = e.target.files?.[0];
  if (!f) return;

  // local preview
  setPreviewUrl(URL.createObjectURL(f));

  try {
    await changeProfile.upload(f); // 🚀 real upload
    setPreviewUrl(null); // reset so Avatar uses backend URL
  } catch (err) {
    console.error(err);
  }
};

  return (
    <Box p={6} maxW="720px" mx="auto">
      <HStack justifyContent="space-between" mb={6}>
        <Heading size="lg">Your profile</Heading>
        <Text color="gray.600">{new Date(user.createdAt).toLocaleDateString()}</Text>
      </HStack>

      <Stack direction={["column", "row"]} spacing={6} alignItems="center">
        <Box textAlign="center">
          <Avatar
  size="2xl"
  name={user.fullName || user.username}
  src={previewUrl ?? user.profileImageUrl ?? undefined}
/>
          <Input ref={fileRef} type="file" accept="image/*" display="none" onChange={onFileChange} />
          <Button onClick={onChooseFile} size="sm" variant="outline" mb={2}>
            Change photo
          </Button>
          <Text fontSize="xs" color="gray.500">Local preview only — upload not implemented</Text>
        </Box>

        <Box flex="1">
          <Heading size="md" mb={2}>{user.fullName || user.username}</Heading>
          <Text mb={1}><strong>Username:</strong> {user.username}</Text>
          <Text mb={1}><strong>Email:</strong> {user.email}</Text>
          <Text mb={1}><strong>Role:</strong> {user.role}</Text>
          <Text mb={1}><strong>ID:</strong> {user.id}</Text>
        </Box>
      </Stack>
    </Box>
  );
};

export default UserDetailPage;