import { useMutation } from "@tanstack/react-query";
import { Movie } from "./movie";
import { useQueryClient } from "@tanstack/react-query";
import { axiosInstance } from "../../services/api-client";


export interface MovieCreate {
  title: string;
  tmdbId: number;
  overview?: string;
  releaseDate?: string;
  runtime?: number;
  posterUrl?: string;
}

//const api = new ApiClient<Movie>("movies");

const useAddMovie = () => {
  const qc = useQueryClient();
  return useMutation<Movie, Error, MovieCreate>({
    mutationFn: (payload) => axiosInstance.post("/movies",payload),
    onSuccess: () => qc.invalidateQueries(["movies"]),
  });
};

export default useAddMovie;