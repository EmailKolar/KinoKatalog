import axios, { AxiosRequestConfig } from "axios";

export const AUTH_KEY = "kk_auth_token";

export const axiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_URL,
});

const saved = localStorage.getItem(AUTH_KEY);
if (saved) {
  axiosInstance.defaults.headers.common["Authorization"] = saved;
}

// set or clear Authorization header and persist token
export function setAuthHeader(token: string | null) {
  if (token) {
    localStorage.setItem(AUTH_KEY, token);
    axiosInstance.defaults.headers.common["Authorization"] = `Bearer ${token}`;
  } else {
    localStorage.removeItem(AUTH_KEY);
    delete axiosInstance.defaults.headers.common["Authorization"];
  }
}

// initialize from storage on module load
(function initAuthFromStorage() {
  try {
    const saved = localStorage.getItem(AUTH_KEY);
    if (saved) {
      axiosInstance.defaults.headers.common["Authorization"] = `Bearer ${saved}`;
    }
  } catch (e) {
    // ignore (e.g. SSR or private mode)
  }
})();

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