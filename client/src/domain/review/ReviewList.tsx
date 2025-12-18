import { VStack, Box, Text, HStack } from "@chakra-ui/react";
import { useEffect, useRef } from "react";
import { Review } from "./Review";

interface Props {
  reviews?: Review[];
}

const ReviewList = ({ reviews }: Props) => {
  if (!reviews || reviews.length === 0) return <Text>No reviews yet.</Text>;

  return (
    <VStack align="stretch" spacing={4}>
      {reviews.map((r) => {
        const contentRef = useRef<HTMLParagraphElement>(null);

        useEffect(() => {
          if (contentRef.current) {
     
            console.log("Raw review content:", r.content);
            console.log("Rendered textContent:", contentRef.current.textContent);
            console.log("Rendered innerHTML:", contentRef.current.innerHTML);
            console.log("       ")
          }
        }, [r]);

        return (
          <Box key={r.id} padding={4} borderWidth="1px" borderRadius="md">
            <HStack justifyContent="space-between" mb={2}>
              <Text fontWeight="bold">{r.author ?? "Anonymous"}</Text>
              <Text color="gray.500" fontSize="sm">
                {new Date(r.createdAt).toLocaleString()}
              </Text>
            </HStack>

            {/* The key part: attach the ref */}
            <Text ref={contentRef} mb={2}>
              {r.content}
            </Text>

            {typeof r.rating !== "undefined" && (
              <Text fontSize="sm" color="gray.600">
                Rating: {r.rating}/10
              </Text>
            )}
          </Box>
        );
      })}
    </VStack>
  );
};

export default ReviewList;
