import React, { useState, useEffect } from "react";
import {
  VStack,
  Textarea,
  Button,
  Select,
  Text,
  FormControl,
  FormLabel,
} from "@chakra-ui/react";
import useAddReview from "./useAddReview";

interface Props {
  movieId: string | number;
}

const ReviewForm = ({ movieId }: Props) => {
  const [content, setContent] = useState("");
  const [rating, setRating] = useState<string>(""); // string so Select can use empty value

  const mutation = useAddReview(movieId);

  useEffect(() => {
    if (mutation.isSuccess) {
      setContent("");
      setRating("");
    }
  }, [mutation.isSuccess]);

  const handleSubmit = (e?: React.FormEvent) => {
    e?.preventDefault();
    if (!content.trim()) return;
    if (rating === "") return; // rating required
    mutation.mutate({
      reviewText: content.trim(),
      rating: Number(rating),
    });
  };

  // Convert unknown mutation.error into a safe string/ReactNode
  const errorMessage: string | null = mutation.error
    ? mutation.error instanceof Error
      ? mutation.error.message
      : typeof mutation.error === "string"
      ? mutation.error
      : JSON.stringify(mutation.error)
    : null;

  return (
    <VStack as="form" spacing={3} onSubmit={handleSubmit} align="stretch">
      <FormControl isRequired>
        <FormLabel htmlFor="review-content">Review</FormLabel>
        <Textarea
          id="review-content"
          placeholder="Write your review..."
          value={content}
          onChange={(e) => setContent(e.target.value)}
          isRequired
        />
      </FormControl>

      <FormControl isRequired>
        <FormLabel htmlFor="review-rating" id="review-rating-label">
          Rating (0–10)
        </FormLabel>
        <Select
          id="review-rating"
          aria-labelledby="review-rating-label"
          title="Rating (0–10)"
          placeholder="Select rating (required)"
          value={rating}
          onChange={(e) => setRating(e.target.value)}
          isRequired
        >
          {Array.from({ length: 11 }).map((_, i) => (
            <option key={i} value={i}>
              {i}
            </option>
          ))}
        </Select>
      </FormControl>

      {errorMessage && <Text color="tomato">{errorMessage}</Text>}

      <Button
        type="submit"
        isLoading={mutation.isLoading}
        colorScheme="blue"
        alignSelf="flex-start"
      >
        Add review
      </Button>
    </VStack>
  );
};

export default ReviewForm;