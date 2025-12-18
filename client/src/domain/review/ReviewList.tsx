import { VStack, Text } from "@chakra-ui/react";
import { Review } from "./Review";
import ReviewItem from "./ReviewItem";

interface Props {
  reviews?: Review[];
}

const ReviewList = ({ reviews }: Props) => {
  if (!reviews || reviews.length === 0) return <Text>No reviews yet.</Text>;

  return (
    <VStack align="stretch" spacing={4}>
      {reviews.map((r) => (
        <ReviewItem key={r.id} review={r} />
      ))}
    </VStack>
  );
};

export default ReviewList;
