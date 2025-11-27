import axios, { AxiosRequestConfig } from "axios";

const axiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_URL,
  params: {
    key: import.meta.env.VITE_API_KEY,
  },
});

class ApiClient<T> {
  private endpoint: string;

  constructor(endpoint: string) {
    this.endpoint = endpoint;
  };

  getAll = (config?: AxiosRequestConfig) => 
    axiosInstance.get<Response<T>>(this.endpoint, config).then((res) => res.data);
    
  get = (id: number |string) => 
    axiosInstance.get<T>(this.endpoint + "/" + id).then((res) => res.data);
};


export interface Response<T> {
  count: number;
  results: T[];
  next: string | null;
}
export default ApiClient;