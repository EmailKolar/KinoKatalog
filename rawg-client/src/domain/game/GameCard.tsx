import { Card, CardBody, Heading, HStack, Image } from "@chakra-ui/react";
import { Game } from "./Game";
import PlatformIconsList from "../platform/PlatformIconsList";
import CriticScore from "./CriticScore";
import getCroppedImageUrl from "../../services/image-url";
import { Link } from "react-router-dom";

interface Props {
  game: Game;
}

const GameCard = ({ game }: Props) => {
  return (
    <Card overflow="hidden" borderRadius="10">
      <Image src={getCroppedImageUrl(game.background_image)} />
      <CardBody>
        <HStack justifyContent="space-between">
          <PlatformIconsList
            platforms={game.parent_platforms.map((p) => p.platform)}
          />
          <CriticScore score={game.metacritic} />
        </HStack>
        <Heading fontSize="2xl" marginTop={2}>
          <HStack>
            <Link to={`/games/${game.id}`}>{game.name}</Link>
          </HStack>
        </Heading>
      </CardBody>
    </Card>
  );
};

export default GameCard;
