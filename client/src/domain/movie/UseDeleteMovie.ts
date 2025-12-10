import { useMutation, useQueryClient } from "@tanstack/react-query";
import { axiosInstance } from "../../services/api-client";




const useDeleteMovie = () => {
  const qc = useQueryClient();
  return useMutation<void, Error, number>({
    mutationFn: (id) => axiosInstance.delete(`/movies/${id}`).then(() => undefined),
    onSuccess: () => qc.invalidateQueries(["movies"]),
  });
};

export default useDeleteMovie;