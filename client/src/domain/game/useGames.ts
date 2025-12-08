
import { Game } from "./Game";
import { useInfiniteQuery } from "@tanstack/react-query";
import { Response } from "../../services/api-client";
import ApiClient from "../../services/api-client";
import useGameQueryStore from "../../state";

const apiClient = new ApiClient<Game>("/games");


const useGames = () => {

  const gameQuery = useGameQueryStore((s) => s.gameQuery);

  return useInfiniteQuery<Response<Game>, Error>({
  queryKey: ["games", gameQuery],
  queryFn: ({ pageParam=1 }) => 
    apiClient.getAll({
      params: {
        genres: gameQuery.genreId,
        parent_platforms: gameQuery.platformId,
        stores: gameQuery.storeId,
        ordering: gameQuery.sortOrder,
        search: gameQuery.searchText,
        page: pageParam,
      },
    }),
    getNextPageParam: (lastPage, allPages) => {
      return lastPage.next ? allPages.length + 1 : undefined;
    }
});
}

//useGames is a custom hook that fetches games from the /games endpoint
//it takes an optional selectedGenre parameter to filter games by genre
//it also takes an optional dependencies parameter to refetch data when the selectedGenre changes
/* const useGames = (gameQuery: GameQuery) =>
  useData<Game>("/games", {
    params: {
      genres: gameQuery.genre?.slug,
      parent_platforms: gameQuery.platform?.id,
      stores: gameQuery.store?.id,
      ordering: gameQuery.sortOrder,
      search: gameQuery.searchText
    }
  },
    [gameQuery]); */


export default useGames;

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
