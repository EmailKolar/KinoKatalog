import { useQuery } from "@tanstack/react-query";
import ApiClient from "../../services/api-client";
import { Response } from "../../services/api-client";
import stores from "./stores"; //import the stores data from the data folder
import ms from "ms";
import { Store } from "./Store"; //import the Store interface from the Store file


const apiClient = new ApiClient<Store>("/stores"); //create an instance of the ApiClient with the endpoint /stores

const useStores = () => useQuery<Response<Store>, Error>({
    queryKey: ["stores"],
    queryFn: apiClient.getAll, //fetch the stores from the api and return the data
    staleTime: ms("1d"),
    cacheTime: ms("1d"),
    initialData: stores
});
export default useStores;
