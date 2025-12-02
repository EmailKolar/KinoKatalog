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
import type { Review } from "../domain/review/Review";

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

  // map backend DTO -> frontend Review shape
  const mappedReviews: Review[] | undefined = reviews?.map((r: any) => ({
    id: Number(r.id),
    movie: r.movieId ?? r.movie ?? Number(id),
    author: r.username ?? r.author ?? "Anonymous",
    content: r.reviewText ?? r.content ?? "",
    rating:
      typeof r.rating === "number"
        ? r.rating
        : typeof r.rating === "string"
        ? Number(r.rating)
        : undefined,
    createdAt: r.createdAt ?? r.createdAtString ?? new Date().toISOString(),
  }));

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
          <ReviewList reviews={mappedReviews} />
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