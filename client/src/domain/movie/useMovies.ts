import { Movie } from "./movie";
import { useQuery } from "@tanstack/react-query";
import { Response } from "../../services/api-client";
import ApiClient from "../../services/api-client";
import useMovieQueryStore from "../../state";

const apiClient = new ApiClient<Movie>("movies");

const useMovies = () => {
  const { searchText } = useMovieQueryStore((s) => s.movieQuery);

  return useQuery<Movie[], Error>({
    queryKey: ["movies", searchText],
    queryFn: () =>
      apiClient.getAll({
        params: {
          q: searchText,
        },
      }),
  });
};

export default useMovies;
