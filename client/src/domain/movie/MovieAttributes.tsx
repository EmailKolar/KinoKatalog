import { SimpleGrid, Text, Box, Image } from "@chakra-ui/react";
import { Movie } from "./movie";
import DefinitionItem from "../../components/DefinitionItem";

interface Props {
  movie: Movie;
}

const formatDate = (iso?: string) =>
  iso ? new Date(iso).toLocaleDateString() : "N/A";

const formatRuntime = (mins?: number) =>
  typeof mins === "number" && mins > 0
    ? `${Math.floor(mins / 60)}h ${mins % 60}m`
    : "N/A";

const MovieAttributes = ({ movie }: Props) => {
  return (
    <SimpleGrid columns={2} as="dl" gap={4}>
      <DefinitionItem term="Overview">
        <Text>{movie.overview || "No overview available."}</Text>
      </DefinitionItem>

      <DefinitionItem term="Poster">
        {movie.posterUrl ? (
          <Box maxW="200px">
            <Image src={movie.posterUrl} alt={movie.title} borderRadius="md" />
          </Box>
        ) : (
          <Text>No poster</Text>
        )}
      </DefinitionItem>

      <DefinitionItem term="Release Date">
        <Text>{formatDate(movie.releaseDate)}</Text>
      </DefinitionItem>

      <DefinitionItem term="Runtime">
        <Text>{formatRuntime(movie.runtime)}</Text>
      </DefinitionItem>

      <DefinitionItem term="Average Rating">
        <Text>{movie.averageRating ?? "N/A"}</Text>
      </DefinitionItem>

      <DefinitionItem term="Review Count">
        <Text>{movie.reviewCount ?? 0}</Text>
      </DefinitionItem>

      <DefinitionItem term="TMDB ID">
        <Text>{movie.tmdbId ?? "N/A"}</Text>
      </DefinitionItem>

      <DefinitionItem term="Created At">
        <Text>{formatDate(movie.createdAt)}</Text>
      </DefinitionItem>
    </SimpleGrid>
  );
};

export default MovieAttributes;