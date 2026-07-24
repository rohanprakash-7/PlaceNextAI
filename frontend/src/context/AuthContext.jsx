import { createContext, useCallback, useContext, useEffect, useRef, useState } from "react";
import { useNavigate } from "react-router-dom";
import * as authService from "../services/authService";
import {
  getToken,
  setToken,
  removeToken,
  isTokenExpired,
  getTokenExpiryMs,
} from "../services/tokenService";

export const ROLE_HOME = {
  ROLE_STUDENT: "/dashboard/student",
  ROLE_RECRUITER: "/dashboard/recruiter",
  ROLE_ADMIN: "/dashboard/admin",
  ROLE_ALUMNI: "/dashboard/alumni",
};

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const navigate = useNavigate();
  const [user, setUser] = useState(null);
  const [initializing, setInitializing] = useState(true);
  const expiryTimerRef = useRef(null);

  const clearExpiryTimer = useCallback(() => {
    if (expiryTimerRef.current) {
      clearTimeout(expiryTimerRef.current);
      expiryTimerRef.current = null;
    }
  }, []);

  const logout = useCallback(
    (redirect = true) => {
      clearExpiryTimer();
      removeToken();
      setUser(null);
      if (redirect) {
        navigate("/login", { replace: true });
      }
    },
    [clearExpiryTimer, navigate]
  );

  const scheduleAutoLogout = useCallback(
    (token) => {
      clearExpiryTimer();
      const expiryMs = getTokenExpiryMs(token);
      if (!expiryMs) return;
      const remaining = expiryMs - Date.now();
      if (remaining <= 0) {
        logout();
        return;
      }
      expiryTimerRef.current = setTimeout(() => logout(), remaining);
    },
    [clearExpiryTimer, logout]
  );

  useEffect(() => {
    async function restoreSession() {
      const token = getToken();
      if (!token || isTokenExpired(token)) {
        removeToken();
        setInitializing(false);
        return;
      }
      try {
        const me = await authService.getMe();
        setUser(me);
        scheduleAutoLogout(token);
      } catch {
        removeToken();
        setUser(null);
      } finally {
        setInitializing(false);
      }
    }
    restoreSession();
  }, [scheduleAutoLogout]);

  useEffect(() => {
    const onForcedLogout = () => logout();
    window.addEventListener("auth:logout", onForcedLogout);
    return () => window.removeEventListener("auth:logout", onForcedLogout);
  }, [logout]);

  // A 2xx response with no JWT means the request never actually reached the
  // backend (e.g. the frontend's API base URL is misconfigured and the call
  // landed back on the SPA's own index.html instead). Trusting it silently
  // used to store the literal string "undefined" as the token and navigate
  // home with no visible error - fail loudly instead so it's diagnosable.
  const applyAuthResponse = useCallback(
    async (authResponse) => {
      if (!authResponse || typeof authResponse.token !== "string" || !authResponse.token) {
        const error = new Error("Malformed auth response - no token in the response body.");
        error.friendlyMessage =
          "The server sent back an unexpected response. This usually means the app " +
          "isn't reaching the backend API - check the site's API URL configuration.";
        throw error;
      }
      setToken(authResponse.token);
      const me = await authService.getMe();
      if (!me || typeof me.role !== "string") {
        const error = new Error("Malformed /auth/me response - no role in the response body.");
        error.friendlyMessage = "Signed in, but couldn't load your profile. Please try again.";
        throw error;
      }
      setUser(me);
      scheduleAutoLogout(authResponse.token);
      return me;
    },
    [scheduleAutoLogout]
  );

  const login = useCallback(
    async (credentials) => applyAuthResponse(await authService.login(credentials)),
    [applyAuthResponse]
  );

  const registerStudent = useCallback(
    async (payload) => applyAuthResponse(await authService.registerStudent(payload)),
    [applyAuthResponse]
  );

  const registerRecruiter = useCallback(
    async (payload) => applyAuthResponse(await authService.registerRecruiter(payload)),
    [applyAuthResponse]
  );

  const registerAlumni = useCallback(
    async (payload) => applyAuthResponse(await authService.registerAlumni(payload)),
    [applyAuthResponse]
  );

  const value = {
    user,
    initializing,
    isAuthenticated: Boolean(user),
    login,
    registerStudent,
    registerRecruiter,
    registerAlumni,
    logout,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error("useAuth must be used inside an AuthProvider");
  }
  return context;
}
