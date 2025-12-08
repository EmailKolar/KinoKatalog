/*import { Heading } from "@chakra-ui/react";
import useGenres from "../genre/useGenres";
import useMovieQueryStore from "../../state";

const MovieHeading = () => {
  const { genreId } = useMovieQueryStore((s) => s.movieQuery || {});

  const { data: dataGenres } = useGenres();
  const genre = dataGenres?.results.find((g) => g.id === genreId);

  const heading = `${genre?.name || ""} Movies`;

  return (
    <Heading as="h1" fontSize="5xl" paddingY={5}>
      {heading}
    </Heading>
  );
};

export default MovieHeading; */