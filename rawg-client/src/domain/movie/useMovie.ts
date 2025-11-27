import ApiClient from "../../services/api-client";
import { useQuery } from "@tanstack/react-query";
import { Movie } from "./movie";

const apiClient = new ApiClient<Movie>("movies");

const useMovie = (movieId: number) =>
  useQuery<Movie, Error>({
    queryKey: ["movie", movieId],
    queryFn: () => apiClient.get(movieId),
    staleTime: Infinity,
  });

export default useMovie;