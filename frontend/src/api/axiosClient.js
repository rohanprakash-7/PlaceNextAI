import axios from "axios";
import { getToken, removeToken } from "../services/tokenService";

// VITE_API_BASE_URL is the name used everywhere in this repo (.env.example,
// .env.development, DEPLOYMENT.md). VITE_API_URL is accepted as a fallback
// only to guard against a mistyped/mismatched variable name on a hosting
// dashboard - it is not a second "real" convention, just a safety net.
const isConfigured = Boolean(import.meta.env.VITE_API_BASE_URL || import.meta.env.VITE_API_URL);
const apiBaseUrl = import.meta.env.VITE_API_BASE_URL || import.meta.env.VITE_API_URL || "/api";

if (import.meta.env.PROD && !isConfigured) {
  // eslint-disable-next-line no-console
  console.error(
    "[PlaceNextAI] VITE_API_BASE_URL is not set for this production build. " +
      "API calls will hit this site's own origin instead of a real backend and fail. " +
      "Set VITE_API_BASE_URL in the hosting provider's environment variables and rebuild."
  );
}

const axiosClient = axios.create({
  baseURL: apiBaseUrl,
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
      const attemptedUrl = (error.config?.baseURL || "") + (error.config?.url || "");
      error.friendlyMessage = isConfigured
        ? 'Cannot reach the server at "' +
          attemptedUrl +
          '". If this is a freshly deployed backend on a free hosting tier, it may ' +
          "still be waking up from sleep - try again in a moment. Otherwise, verify " +
          "the backend is running and its CORS settings allow this site's origin."
        : "This deployment has no API URL configured (VITE_API_BASE_URL is missing), " +
          'so the request went to "' +
          attemptedUrl +
          '" instead of a real backend. This is a build configuration issue, not a ' +
          "local backend problem - set VITE_API_BASE_URL and rebuild.";
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
