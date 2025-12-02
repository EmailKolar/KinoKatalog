import { Movie } from "./movie";
import { useQuery } from "@tanstack/react-query";
import { Response } from "../../services/api-client";
import ApiClient from "../../services/api-client";
import useMovieQueryStore from "../../state";

const apiClient = new ApiClient<Movie>("movies");

const useMovies = () => {
  // adjust selector to match your store shape (e.g. movieQuery or searchText)
  const movieQuery = useMovieQueryStore((s) => s.movieQuery);

  return useQuery<Movie[], Error>({
  queryKey: ["movies", movieQuery],
  queryFn: () =>
    apiClient.getAll({
      params: {
        ordering: movieQuery?.sortOrder,
        search: movieQuery?.searchText,
      },
    }),
});
};

export default useMovies;