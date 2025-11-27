import { useQuery } from "@tanstack/react-query";
import { Response } from "../../services/api-client"; //import the Response interface from the api-client to use it in the useQuery hook
import ApiClient from "../../services/api-client"; //import the ApiClient class to use it in the useQuery hook
import platforms from "./platforms";
import ms from "ms";
import { Platform } from "./Platform"; //import the Platform interface from the Platform file


const apiClient = new ApiClient<Platform>("/platforms/lists/parents"); //create an instance of the ApiClient with the endpoint


const usePlatforms = () => useQuery<Response<Platform>, Error>({
  queryKey: ["platforms"],
  queryFn: apiClient.getAll, //fetch the platforms from the api and return the data
  staleTime: ms("1d"),
  cacheTime: ms("1d"),
  initialData: platforms, //set the initial data to an empty array, so the data is not undefined
});

export default usePlatforms;
