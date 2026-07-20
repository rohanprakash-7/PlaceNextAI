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

  const login = useCallback(
    async (credentials) => {
      const authResponse = await authService.login(credentials);
      setToken(authResponse.token);
      const me = await authService.getMe();
      setUser(me);
      scheduleAutoLogout(authResponse.token);
      return me;
    },
    [scheduleAutoLogout]
  );

  const registerStudent = useCallback(
    async (payload) => {
      const authResponse = await authService.registerStudent(payload);
      setToken(authResponse.token);
      const me = await authService.getMe();
      setUser(me);
      scheduleAutoLogout(authResponse.token);
      return me;
    },
    [scheduleAutoLogout]
  );

  const registerRecruiter = useCallback(
    async (payload) => {
      const authResponse = await authService.registerRecruiter(payload);
      setToken(authResponse.token);
      const me = await authService.getMe();
      setUser(me);
      scheduleAutoLogout(authResponse.token);
      return me;
    },
    [scheduleAutoLogout]
  );

  const value = {
    user,
    initializing,
    isAuthenticated: Boolean(user),
    login,
    registerStudent,
    registerRecruiter,
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
