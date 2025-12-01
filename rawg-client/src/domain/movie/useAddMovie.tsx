import { useMutation } from "@tanstack/react-query";
import ApiClient from "../../services/api-client";
import { Movie } from "./movie";
import { useQueryClient } from "@tanstack/react-query";

export interface MovieCreate {
  title: string;
  tmdbId: number;
  overview?: string;
  releaseDate?: string;
  runtime?: number;
  posterUrl?: string;
}

const api = new ApiClient<Movie>("movies");

const useAddMovie = () => {
  const qc = useQueryClient();
  return useMutation<Movie, Error, MovieCreate>({
    mutationFn: (payload) => api.post(payload),
    onSuccess: () => qc.invalidateQueries(["movies"]),
  });
};

export default useAddMovie;