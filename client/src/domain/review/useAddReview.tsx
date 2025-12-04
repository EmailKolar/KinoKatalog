import { useMutation, useQueryClient } from "@tanstack/react-query";
import { axiosInstance } from "../../services/api-client";

type Payload = {
  rating: number;
  reviewText: string;
};

const useAddReview = (movieId?: string | number) => {
  const qc = useQueryClient();

  return useMutation(
    async (newReview: Payload) => {
      if (!movieId) throw new Error("Missing movieId");
      if (typeof newReview.rating !== "number") throw new Error("Rating is required");

      // No need for Authorization header - cookie is sent automatically via withCredentials
      const response = await axiosInstance.post(
        `/reviews/movie/${movieId}`,
        newReview,
      );

      return response.data;
    },
    {
      onSuccess: () => qc.invalidateQueries(["movieReviews", String(movieId)]),
    }
  );
};

export default useAddReview;