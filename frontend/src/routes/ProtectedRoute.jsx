import { Navigate, useLocation } from "react-router-dom";
import { useAuth, ROLE_HOME } from "../context/AuthContext.jsx";
import Loader from "../components/ui/Loader.jsx";

export default function ProtectedRoute({ children, allowedRoles }) {
  const { user, initializing } = useAuth();
  const location = useLocation();

  if (initializing) {
    return <Loader />;
  }

  if (!user) {
    return <Navigate to="/login" state={{ from: location.pathname }} replace />;
  }

  if (allowedRoles && !allowedRoles.includes(user.role)) {
    return <Navigate to={ROLE_HOME[user.role] || "/"} replace />;
  }

  return children;
}
