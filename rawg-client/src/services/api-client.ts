import axios, { AxiosRequestConfig } from "axios";

const AUTH_KEY = "kk_auth_token";

export const axiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_URL,
});

const saved = localStorage.getItem(AUTH_KEY);
if (saved) {
  axiosInstance.defaults.headers.common["Authorization"] = saved;
}

export function setAuthHeader(token: string | null) {
  if (token) {
    const header = `Bearer ${token}`;
    localStorage.setItem(AUTH_KEY, header);
    axiosInstance.defaults.headers.common["Authorization"] = header;
  } else {
    localStorage.removeItem(AUTH_KEY);
    delete axiosInstance.defaults.headers.common["Authorization"];
  }
}

class ApiClient<T> {
  private endpoint: string;

  constructor(endpoint: string) {
    this.endpoint = endpoint;
  }

  getAll = (config?: AxiosRequestConfig) =>
    axiosInstance.get<T[]>(this.endpoint, config).then((res) => res.data);

  get = (id: number | string) =>
    axiosInstance.get<T>(this.endpoint + "/" + id).then((res) => res.data);

  post = (payload: any, config?: AxiosRequestConfig) =>
    axiosInstance.post<T>(this.endpoint, payload, config).then((res) => res.data);

  delete = (id: number | string, config?: AxiosRequestConfig) =>
    axiosInstance.delete<T>(`${this.endpoint}/${id}`, config).then((res) => res.data);
}

export interface Response<T> {
  count: number;
  results: T[];
  next: string | null;
}
export default ApiClient;