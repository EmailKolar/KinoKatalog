import { useQuery } from "@tanstack/react-query";
import ApiClient from "../../services/api-client";
import { Review } from "./Review";

const useMovieReviews = (movieId?: string | number) =>
  useQuery<Review[], Error>({
    queryKey: ["movieReviews", movieId],
    queryFn: async () => {
      if (!movieId) return [];
      const api = new ApiClient<Review>(`reviews/movie/${movieId}`);
      return api.getAll();
    },
    enabled: !!movieId,
  });

export default useMovieReviews;