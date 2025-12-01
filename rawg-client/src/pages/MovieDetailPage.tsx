import { Heading, Spinner, Box, Divider } from "@chakra-ui/react";
import useMovie from "../domain/movie/useMovie";
import useMovieReviews from "../domain/review/UseMovieReviews";
import { useParams } from "react-router-dom";
import ExpandableText from "../components/ExpandableText";
import MovieAttributes from "../domain/movie/MovieAttributes";
import ReviewList from "../domain/review/ReviewList";
import ReviewForm from "../domain/review/ReviewForm";
import { useAuth } from "../services/auth";
import React from "react";

const MovieDetailPage = () => {
  const { id } = useParams();
  const { data: movie, error, isLoading } = useMovie(id);
  const {
    data: reviews,
    error: reviewsError,
    isLoading: reviewsLoading,
  } = useMovieReviews(id);

  const auth = useAuth();

  if (isLoading) return <Spinner />;
  if (error || !movie) throw error;

  return (
    <>
      <Heading>{movie.title}</Heading>
      <ExpandableText>{movie.overview}</ExpandableText>
      <MovieAttributes movie={movie} />

      <Divider my={6} />

      <Box mb={4}>
        <Heading size="md" mb={4}>
          Reviews
        </Heading>

        {reviewsLoading ? (
          <Spinner />
        ) : reviewsError ? (
          <Box color="tomato">{reviewsError.message}</Box>
        ) : (
          <ReviewList reviews={reviews} />
        )}
      </Box>

      <Box mt={6}>
        <Heading size="sm" mb={3}>
          Add a review
        </Heading>
        {auth.isAuthenticated ? (
          <ReviewForm movieId={id ?? ""} />
        ) : (
          <Box color="gray.500">You must be logged in to leave a review.</Box>
        )}
      </Box>
    </>
  );
};

export default MovieDetailPage;