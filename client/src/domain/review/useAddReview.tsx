import { useMutation, useQueryClient } from "@tanstack/react-query";
import useAuthStore from "../../state";
import { axiosInstance } from "../../services/api-client";

type Payload = {
  rating: number;
  reviewText: string;
};

const useAddReview = (movieId?: string | number) => {
  const qc = useQueryClient();
  const token = useAuthStore((s: any) => s.token);

  return useMutation(
    async (newReview: Payload) => {
      if (!movieId) throw new Error("Missing movieId");
      if (typeof newReview.rating !== "number") throw new Error("Rating is required");

      const storedToken =
        typeof token === "string"
          ? token
          : localStorage.getItem("kk_auth_token") ?? undefined;

      if (!storedToken) {
        throw new Error("Not authenticated. Please log in to add a review.");
      }

      const authHeader = storedToken.startsWith("Bearer ")
        ? storedToken
        : `Bearer ${storedToken}`;

      // send Authorization per-request and call controller under /api/reviews
      const response = await axiosInstance.post(
        `/reviews/movie/${movieId}`,
        newReview,
        { headers: { Authorization: authHeader } }
      );

      return response.data;
    },
    {
      onSuccess: () => qc.invalidateQueries(["movieReviews", String(movieId)]),
    }
  );
};

export default useAddReview;