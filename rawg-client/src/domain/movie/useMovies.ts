/*
import { Movie } from "./movie";
import { useInfiniteQuery } from "@tanstack/react-query";
import { Response } from "../../services/api-client";
import ApiClient from "../../services/api-client";
import useMovieQueryStore from "../../state";

const apiClient = new ApiClient<Movie>("/movies");

const useMovies = () => {
  // adjust selector to match your store shape (e.g. movieQuery or searchText)
  const movieQuery = useMovieQueryStore((s) => s.movieQuery);

  return useInfiniteQuery<Response<Movie>, Error>({
    queryKey: ["movies", movieQuery],
    queryFn: ({ pageParam = 1 }) =>
      apiClient.getAll({
        params: {
          // map your movieQuery fields to the API params you need
          genres: movieQuery?.genreId,
          providers: movieQuery?.providerId,
          ordering: movieQuery?.sortOrder,
          search: movieQuery?.searchText,
          page: pageParam,
        },
      }),
    getNextPageParam: (lastPage, allPages) => {
      return lastPage.next ? allPages.length + 1 : undefined;
    },
  });
};

export default useMovies; */