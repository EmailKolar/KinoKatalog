import React, { useRef, useState, useEffect } from "react";
import { useParams } from "react-router-dom";
import { useQuery } from "@tanstack/react-query";
import ApiClient from "../services/api-client";
import { User } from "../domain/user/user";
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

const api = new ApiClient<User>("users");

const UserDetailPage: React.FC = () => {
  const { id } = useParams();
  const fileRef = useRef<HTMLInputElement | null>(null);
  const [previewUrl, setPreviewUrl] = useState<string | null>(null);

  const { data: user, isLoading, error } = useQuery<User, Error>(
    ["user", id],
    () => api.get(id as string),
    {
      enabled: !!id,
      staleTime: 1000 * 60 * 5,
    }
  );

  useEffect(() => {
    return () => {
      // revoke object url on unmount if we created one
      if (previewUrl) URL.revokeObjectURL(previewUrl);
    };
  }, [previewUrl]);

  if (isLoading) return <Spinner />;
  if (error) return <Box color="tomato">Error: {error.message}</Box>;
  if (!user) return <Box>No user found</Box>;

  const onChooseFile = () => fileRef.current?.click();

  const onFileChange: React.ChangeEventHandler<HTMLInputElement> = (e) => {
    const f = e.target.files?.[0];
    if (!f) return;
    const url = URL.createObjectURL(f);
    setPreviewUrl(url);
    // NOTE: uploading to server not implemented here.
  };

  return (
    <Box p={6} maxW="720px" mx="auto">
      <HStack justifyContent="space-between" mb={6}>
        <Heading size="lg">User profile</Heading>
        <Text color="gray.600">{new Date(user.createdAt).toLocaleDateString()}</Text>
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
          <Text fontSize="xs" color="gray.500">
            Local preview only — upload not implemented
          </Text>
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