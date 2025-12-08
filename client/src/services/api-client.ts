import axios, { AxiosRequestConfig } from "axios";

// Create axios instance with credentials enabled
export const axiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_URL,
  withCredentials: true, // CRITICAL: This sends cookies automatically
});

// Helper to get CSRF token from cookie
function getCsrfToken(): string | null {
  const name = "XSRF-TOKEN=";
  const cookies = document.cookie.split(";");
  for (let cookie of cookies) {
    cookie = cookie.trim();
    if (cookie.startsWith(name)) {
      return cookie.substring(name.length);
    }
  }
  return null;
}

// Add CSRF token to all state-changing requests
axiosInstance.interceptors.request.use((config) => {
  const csrfToken = getCsrfToken();
  if (csrfToken && config.method && ["post", "put", "delete", "patch"].includes(config.method.toLowerCase())) {
    config.headers["X-XSRF-TOKEN"] = csrfToken;
  }
  return config;
});

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