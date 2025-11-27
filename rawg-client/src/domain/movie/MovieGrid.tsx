import { SimpleGrid, Spinner, Text } from "@chakra-ui/react";
import useMovies from "./useMovies";
import MovieCard from "./MovieCard";
import MovieCardSkeleton from "./MovieCardSkeleton";
import MovieCardContainer from "./MovieCardContainer";
import React from "react";
import InfiniteScroll from "react-infinite-scroll-component";

const MovieGrid = () => {
  const skeletons = [...Array(20).keys()];

  const { data, error, isLoading, fetchNextPage, hasNextPage } = useMovies();

  if (error) return <Text color="tomato">{error.message}</Text>;

  const fetchedMoviesCount =
    data?.pages.reduce((total, page) => total + page.results.length, 0) || 0;

  return (
    <InfiniteScroll
      dataLength={fetchedMoviesCount}
      next={fetchNextPage}
      hasMore={!!hasNextPage}
      loader={<Spinner />}
      scrollThreshold={1}
    >
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
          : data?.pages.map((page, index) => (
              <React.Fragment key={index}>
                {page.results.map((movie) => (
                  <MovieCardContainer key={movie.id}>
                    <MovieCard movie={movie} />
                  </MovieCardContainer>
                ))}
              </React.Fragment>
            ))}
      </SimpleGrid>
    </InfiniteScroll>
  );
};

export default MovieGrid;