import ApiClient from "../../services/api-client";
import { useQuery } from "@tanstack/react-query";
import { Movie } from "./movie";

const apiClient = new ApiClient<Movie>("movies");

const useMovie = (movieId?: number | string) =>
  useQuery<Movie, Error>({
    queryKey: ["movie", movieId],
    queryFn: () => apiClient.get(movieId as number | string),
    enabled:
      movieId !== undefined &&
      movieId !== null &&
      !(typeof movieId === "number" && Number.isNaN(movieId)),
    staleTime: Infinity,
  });

export default useMovie;