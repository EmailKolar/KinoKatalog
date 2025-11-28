import { SimpleGrid, Spinner, Text } from "@chakra-ui/react";
import useMovies from "./useMovies";
import MovieCard from "./MovieCard";
import MovieCardSkeleton from "./MovieCardSkeleton";
import MovieCardContainer from "./MovieCardContainer";
import React from "react";

const MovieGrid = () => {
  const skeletons = [...Array(20).keys()];
  const { data, error, isLoading } = useMovies();

  if (error) return <Text color="tomato">{error.message}</Text>;

  return (
    <SimpleGrid
      columns={{ base: 1, md: 2, lg: 3, xl: 4 }}
      spacing={4}
      paddingY={10}
    >
      {isLoading
        ? skeletons.map((skeleton) => (
            <MovieCardContainer key={skeleton}>
              <MovieCardSkeleton />
            </MovieCardContainer>
          ))
        : data && data.length > 0
        ? data.map((movie) => (
            <MovieCardContainer key={movie.id}>
              <MovieCard movie={movie} />
            </MovieCardContainer>
          ))
        : (
          <Text>No movies found.</Text>
        )}
    </SimpleGrid>
  );
};

export default MovieGrid;
