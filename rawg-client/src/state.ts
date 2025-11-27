import { create } from "zustand";

interface GameQuery {
  genreId?: number;
  platformId?: number;
  storeId?: number;
  sortOrder: string;
  searchText: string;
}

interface GameQueryStore {
  gameQuery: GameQuery;
  setGenreId: (genreId?: number) => void;
  setPlatformId: (platformId?: number) => void;
  setStoreId: (storeId?: number) => void;
  setSortOrder: (sortOrder: string) => void;
  setSearchText: (searchText: string) => void;
}

const useGameQueryStore = create<GameQueryStore>((set) => ({
  gameQuery: {} as GameQuery,
  setGenreId: (genreId) =>
    set((state) => ({ gameQuery: { ...state.gameQuery, genreId } })),
  setPlatformId: (platformId) =>
    set((state) => ({ gameQuery: { ...state.gameQuery, platformId } })),
  setStoreId: (storeId) =>
    set((state) => ({ gameQuery: { ...state.gameQuery, storeId } })),
  setSortOrder: (sortOrder) =>
    set((state) => ({ gameQuery: { ...state.gameQuery, sortOrder } })),
  setSearchText: (searchText) =>
    set((state) => ({ gameQuery: { ...state.gameQuery, searchText } })),
}));

export default useGameQueryStore;

// use this when API is up and running

/* import type { Movie } from './domain/movie/movie';

type MovieQueryState = {
  searchText: string;
  setSearchText: (text: string) => void;
  clearSearch: () => void;

  results: Movie[];
  setResults: (movies: Movie[]) => void;
  clearResults: () => void;

  loading: boolean;
  setLoading: (v: boolean) => void;

  error: string | null;
  setError: (e: string | null) => void;
};

const useMovieQueryStore = create<MovieQueryState>((set) => ({
  searchText: '',
  setSearchText: (text: string) => set({ searchText: text }),

  clearSearch: () => set({ searchText: '' }),

  results: [],
  setResults: (movies: Movie[]) => set({ results: movies }),
  clearResults: () => set({ results: [] }),

  loading: false,
  setLoading: (v: boolean) => set({ loading: v }),

  error: null,
  setError: (e: string | null) => set({ error: e }),
}));

export default useMovieQueryStore; */