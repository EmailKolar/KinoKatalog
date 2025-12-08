import { Card, CardBody, Heading, HStack, Image, Text } from "@chakra-ui/react";
import { Movie } from "./movie";
import CriticScore from "../game/CriticScore";
import { Link } from "react-router-dom";

interface Props {
  movie: Movie;
}

const MovieCard = ({ movie }: Props) => {
  return (
    <Card overflow="hidden" borderRadius="10">
      {movie.posterUrl && <Image src={movie.posterUrl} alt={movie.title} />}
      <CardBody>
        <HStack justifyContent="space-between">
          <Text fontSize="sm" color="gray.500">
            {movie.releaseDate ? movie.releaseDate.slice(0, 4) : "—"}
          </Text>
          <CriticScore score={Math.round(movie.averageRating)} />
        </HStack>

        <Heading fontSize="2xl" marginTop={2}>
          <HStack>
            <Link to={`/movies/${movie.id}`}>{movie.title}</Link>
          </HStack>
        </Heading>
      </CardBody>
    </Card>
  );
};

export default MovieCard;