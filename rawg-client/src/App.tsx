import { Box, Grid, GridItem, HStack, Show } from "@chakra-ui/react";
import GameGrid from "./domain/game/GameGrid";
import useGenres from "./domain/genre/useGenres";
import PlatformSelector from "./domain/platform/PlatformSelector";
import useStores from "./domain/store/useStores";
import CustomList from "./components/CustomList";
import SortSelector from "./components/SortSelector";
import GameHeading from "./domain/game/GameHeading";
import useGameQueryStore from "./state";

function App() {
  const { genreId, storeId } = useGameQueryStore((s) => s.gameQuery);
  const setGenreId = useGameQueryStore((s) => s.setGenreId);
  const setStoreId = useGameQueryStore((s) => s.setStoreId);

  return (
    <Grid
      paddingX="4"
      templateAreas={{
        base:  "main",
        lg: `"header header" "aside main"`,
      }}
      templateColumns={{ base: "1fr", lg: "200px 1fr" }}
    >

      <Show above="lg">
        <GridItem area={"aside"}>
          <CustomList
            title="Genres"
            onSelectedItemId={setGenreId}
            selectedItemId={genreId}
            useDataHook={useGenres}
          />
          <CustomList
            title="Stores"
            onSelectedItemId={setStoreId}
            selectedItemId={storeId}
            useDataHook={useStores}
          />
        </GridItem>
      </Show>
      <GridItem area={"main"}>
        <Box paddingLeft={2}>
          <GameHeading />
          <HStack>
            <PlatformSelector />
            <SortSelector />
          </HStack>
          <GameGrid />
        </Box>
      </GridItem>
    </Grid>
  );
}

export default App;