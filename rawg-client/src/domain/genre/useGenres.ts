import { useQuery } from "@tanstack/react-query";
import ApiClient from "../../services/api-client";
import { Response } from "../../services/api-client";
//import useData from "./useData";
import genres from "./genres";
import ms from "ms";
import { Genre } from "./Genre"; //import the Genre interface from the Genre file

/* const useGenres = () =>  useData<Genre>("/genres"); //pass the endpoint to the useData hook to fetch the genres from the useGenres hook
//cleaner solution to fetch genres from the useGenres hook, so we don't need to specify the endpoint in the component, 
// which should only be concerned with rendering the data */

const apiClient = new ApiClient<Genre>("/genres"); //create an instance of the ApiClient with the endpoint /genres

const useGenres = () => useQuery<Response<Genre>, Error>({ //useQuery is a hook that fetches data from the api and returns the data from react-query. Provide the generics

    queryKey: ["genres"],
    queryFn: apiClient.getAll,
    staleTime: ms("1d"), 
    cacheTime: ms("1d"), 
    initialData: genres
})
export default useGenres;
