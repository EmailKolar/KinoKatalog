import { useMutation, useQueryClient } from "@tanstack/react-query";
import ApiClient from "../../services/api-client";
import { Review, ReviewCreate } from "./Review";
import useAuthStore from "../../state"; // adjust selector if your auth store has a different hook/name

const useAddReview = (movieId?: string | number) => {
  const qc = useQueryClient();
  const currentUser = useAuthStore((s: any) => s.user); // expecting { username, ... } or null

  return useMutation<Review, Error, ReviewCreate>({
  mutationFn: (newReview) => {
    if (!movieId) return Promise.reject(new Error("Missing movieId"));
    if (typeof newReview.rating !== "number")
      return Promise.reject(new Error("Rating is required"));
    // changed to post to /reviews and include movie id in payload
    const api = new ApiClient<Review>(`reviews`);
    const payload = {
      ...newReview,
      movie: Number(movieId),
      author: newReview.author ?? currentUser?.username ?? currentUser?.name,
    };
    return api.post(payload);
  },
  onSuccess: () => qc.invalidateQueries(["movieReviews", movieId]),
});
};

export default useAddReview;