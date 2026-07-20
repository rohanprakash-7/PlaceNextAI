import axios from "axios";
import { getToken, removeToken } from "../services/tokenService";

const axiosClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || "/api",
  timeout: 15000,
  headers: { "Content-Type": "application/json" },
});

axiosClient.interceptors.request.use((config) => {
  const token = getToken();
  if (token) {
    config.headers.Authorization = "Bearer " + token;
  }
  return config;
});

const AUTH_ENDPOINTS = ["/auth/login", "/auth/register", "/recruiter/register"];

axiosClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (!error.response) {
      error.friendlyMessage =
        "Cannot reach the server. Make sure the backend is running on port 8080.";
      return Promise.reject(error);
    }

    const url = error.config?.url || "";
    const isAuthCall = AUTH_ENDPOINTS.some((endpoint) => url.includes(endpoint));

    if (error.response.status === 401 && !isAuthCall) {
      removeToken();
      window.dispatchEvent(new Event("auth:logout"));
      error.friendlyMessage = "Your session has expired. Please sign in again.";
    } else {
      error.friendlyMessage =
        error.response.data?.message ||
        (error.response.status === 403
          ? "You do not have permission to perform this action."
          : "Something went wrong. Please try again.");
    }

    if (error.response.data?.fieldErrors) {
      error.fieldErrors = error.response.data.fieldErrors;
    }

    return Promise.reject(error);
  }
);

export default axiosClient;
