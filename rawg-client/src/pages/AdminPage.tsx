import React, { useState } from "react";
import {
  Heading,
  Spinner,
  Box,
  Stack,
  Button,
  IconButton,
  useDisclosure,
  Modal,
  ModalOverlay,
  ModalContent,
  ModalHeader,
  ModalBody,
  ModalFooter,
  FormControl,
  FormLabel,
  Input,
  NumberInput,
  NumberInputField,
  HStack,
  Text,
  Icon,
} from "@chakra-ui/react";
import { MdDelete } from "react-icons/md";
import useMovies from "../domain/movie/useMovies";
import useAddMovie from "../domain/movie/useAddMovie";
import useDeleteMovie from "../domain/movie/UseDeleteMovie";

const AdminPage = () => {
  const { data: movies, isLoading, error } = useMovies();
  const { isOpen, onOpen, onClose } = useDisclosure();
  const addMovie = useAddMovie();
  const deleteMovie = useDeleteMovie();

  // simple form state
  const [title, setTitle] = useState("");
  const [tmdbId, setTmdbId] = useState<number | undefined>(undefined);
  const [overview, setOverview] = useState("");
  const [releaseDate, setReleaseDate] = useState("");
  const [runtime, setRuntime] = useState<number | undefined>(undefined);
  const [posterUrl, setPosterUrl] = useState("");

  const submitAdd = async () => {
    await addMovie.mutateAsync({
      title,
      tmdbId: tmdbId ?? 0,
      overview,
      releaseDate,
      runtime: runtime ?? 0,
      posterUrl,
    });
    // reset & close
    setTitle("");
    setTmdbId(undefined);
    setOverview("");
    setReleaseDate("");
    setRuntime(undefined);
    setPosterUrl("");
    onClose();
  };

  if (isLoading) return <Spinner />;
  if (error) return <Box color="tomato">{error.message}</Box>;

  return (
    <Box p={6}>
      <HStack justifyContent="space-between" mb={6}>
        <Heading size="lg">Admin — Movies</Heading>
        <Button colorScheme="blue" onClick={onOpen}>
          Add movie
        </Button>
      </HStack>

      <Stack spacing={3}>
        {movies && movies.length ? (
          movies.map((m) => (
            <HStack
              key={m.id}
              p={3}
              borderWidth="1px"
              borderRadius="md"
              justifyContent="space-between"
            >
              <Box>
                <Text fontWeight="bold">{m.title}</Text>
                <Text fontSize="sm" color="gray.600">
                  id: {m.id} tmdb: {m.tmdbId}
                </Text>
              </Box>
              <IconButton
                aria-label="Delete movie"
                colorScheme="red"
                icon={<Icon as={MdDelete} />}
                onClick={() => deleteMovie.mutate(m.id)}
                isLoading={deleteMovie.isLoading}
              />
            </HStack>
          ))
        ) : (
          <Text>No movies found</Text>
        )}
      </Stack>

      <Modal isOpen={isOpen} onClose={onClose}>
        <ModalOverlay />
        <ModalContent>
          <ModalHeader>Add movie</ModalHeader>
          <ModalBody>
            <FormControl mb={3}>
              <FormLabel>Title</FormLabel>
              <Input value={title} onChange={(e) => setTitle(e.target.value)} />
            </FormControl>

            <FormControl mb={3}>
              <FormLabel>TMDB Id</FormLabel>
              <NumberInput>
                <NumberInputField
                  value={tmdbId ?? ""}
                  onChange={(e) => setTmdbId(Number(e.target.value) || undefined)}
                />
              </NumberInput>
            </FormControl>

            <FormControl mb={3}>
              <FormLabel>Overview</FormLabel>
              <Input value={overview} onChange={(e) => setOverview(e.target.value)} />
            </FormControl>

            <FormControl mb={3}>
              <FormLabel>Release date</FormLabel>
              <Input value={releaseDate} onChange={(e) => setReleaseDate(e.target.value)} placeholder="YYYY-MM-DD" />
            </FormControl>

            <FormControl mb={3}>
              <FormLabel>Runtime</FormLabel>
              <NumberInput>
                <NumberInputField
                  value={runtime ?? ""}
                  onChange={(e) => setRuntime(Number(e.target.value) || undefined)}
                />
              </NumberInput>
            </FormControl>

            <FormControl mb={3}>
              <FormLabel>Poster URL</FormLabel>
              <Input value={posterUrl} onChange={(e) => setPosterUrl(e.target.value)} />
            </FormControl>
          </ModalBody>

          <ModalFooter>
            <Button mr={3} onClick={onClose}>
              Cancel
            </Button>
            <Button colorScheme="blue" onClick={submitAdd} isLoading={addMovie.isLoading}>
              Create
            </Button>
          </ModalFooter>
        </ModalContent>
      </Modal>
    </Box>
  );
};

export default AdminPage;