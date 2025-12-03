import { useAuth } from "../../services/auth";
import.meta.env;

const url = import.meta.env.VITE_API_URL;

export function useChangeProfilePicture() {
  const auth = useAuth();

  const upload = async (file: File) => {
    if (!auth.user) throw new Error("User not logged in");

    const userId = auth.user.id;

    // Request presigned PUT URL - send metadata, server chooses objectKey
    const presignRes = await fetch(`${url}/users/${userId}/profile-image/presign`, {
      method: "POST",
      credentials: "include",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ filename: file.name, contentType: file.type, size: file.size }),
    });

    if (!presignRes.ok) throw new Error("Failed to get presigned URL");
    const { uploadUrl, objectKey } = await presignRes.json();

    // Upload file directly to MinIO using presigned URL (must set Content-Type)
    const putRes = await fetch(uploadUrl, {
      method: "PUT",
      body: file,
      headers: { "Content-Type": file.type },
    });
    if (!putRes.ok) throw new Error("Failed to upload to MinIO");

    // Confirm upload so backend validates/records the objectKey
    const confirmRes = await fetch(`/api/users/${userId}/profile-image/confirm`, {
      method: "POST",
      credentials: "include",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ objectKey }),
    });
    if (!confirmRes.ok) throw new Error("Failed to confirm upload");
    const updatedUser = await confirmRes.json();

    // refresh auth user
    await auth.refresh();
    return updatedUser;
  };

  return { upload };
}