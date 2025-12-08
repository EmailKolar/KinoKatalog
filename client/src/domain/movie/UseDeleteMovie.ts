import { useMutation, useQueryClient } from "@tanstack/react-query";
import ApiClient from "../../services/api-client";
import { Movie } from "./movie";

const api = new ApiClient<Movie>("movies");

const useDeleteMovie = () => {
  const qc = useQueryClient();
  return useMutation<void, Error, number>({
    mutationFn: (id) => api.delete(id).then(() => undefined),
    onSuccess: () => qc.invalidateQueries(["movies"]),
  });
};

export default useDeleteMovie;