const TOKEN_KEY = "placenextai_token";

export function getToken() {
  return localStorage.getItem(TOKEN_KEY);
}

export function setToken(token) {
  localStorage.setItem(TOKEN_KEY, token);
}

export function removeToken() {
  localStorage.removeItem(TOKEN_KEY);
}

export function decodeToken(token) {
  try {
    const payload = token.split(".")[1];
    const normalized = payload.replace(/-/g, "+").replace(/_/g, "/");
    return JSON.parse(atob(normalized));
  } catch {
    return null;
  }
}

export function getTokenExpiryMs(token) {
  const decoded = decodeToken(token);
  if (!decoded || !decoded.exp) return null;
  return decoded.exp * 1000;
}

export function isTokenExpired(token) {
  const expiryMs = getTokenExpiryMs(token);
  if (!expiryMs) return true;
  return Date.now() >= expiryMs;
}
