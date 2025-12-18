import { create } from "zustand";

interface MovieQuery {
  searchText: string;
}

interface MovieQueryStore {
  movieQuery: MovieQuery;
  setSearchText: (text: string) => void;
  clearSearch: () => void;
}

const useMovieQueryStore = create<MovieQueryStore>((set) => ({
  movieQuery: {  searchText: "" },
  setSearchText: (text: string) =>
    set((state) => ({
      movieQuery: { ...state.movieQuery, searchText: text },
    })),
  clearSearch: () =>
    set((state) => ({
      movieQuery: { ...state.movieQuery, searchText: "" },
    })),
}));

export default useMovieQueryStore;
