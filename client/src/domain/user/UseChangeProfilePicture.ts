import { useState } from "react";

const allowedTypes = ["image/jpeg", "image/png", "image/webp"];
const maxSize = 5 * 1024 * 1024; // 5MB

export function useChangeProfilePicture() {
  const [preview, setPreview] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);

  async function onSelectFile(file: File | null) {
    setError(null);
    setPreview(null);

    if (!file) {
      setError("No file selected");
      return false;
    }

    // Check mime type
    if (!allowedTypes.includes(file.type)) {
      setError("Invalid file type");
      return false;
    }

    // Check size
    if (file.size > maxSize) {
      setError("File too large");
      return false;
    }

    // Check image content
    const objectUrl = URL.createObjectURL(file);
    const img = new Image();
    img.src = objectUrl;

    return new Promise<boolean>((resolve) => {
      img.onload = () => {
        setPreview(objectUrl);
        console.log("Valid image selected");
        resolve(true);
      };
      img.onerror = () => {
        setError("Corrupted or invalid image");
        resolve(false);
      };
    });
  }

  return { preview, error, onSelectFile };
}