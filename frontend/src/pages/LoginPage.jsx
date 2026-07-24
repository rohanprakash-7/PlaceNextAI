import { useState } from "react";
import { motion } from "framer-motion";
import { Link, Navigate, useLocation, useNavigate } from "react-router-dom";
import {
  FiMail,
  FiLock,
  FiEye,
  FiEyeOff,
  FiArrowRight,
  FiLoader,
  FiAlertCircle,
} from "react-icons/fi";
import Logo from "../components/ui/Logo.jsx";
import GradientButton from "../components/ui/GradientButton.jsx";
import ThemeToggle from "../components/ui/ThemeToggle.jsx";
import { useAuth, ROLE_HOME } from "../context/AuthContext.jsx";

export default function LoginPage() {
  const navigate = useNavigate();
  const location = useLocation();
  const { user, initializing, login } = useAuth();

  const [form, setForm] = useState({ email: "", password: "" });
  const [showPassword, setShowPassword] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  if (!initializing && user) {
    return <Navigate to={ROLE_HOME[user.role] || "/"} replace />;
  }

  const handleChange = (event) => {
    setForm({ ...form, [event.target.name]: event.target.value });
    setError("");
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setLoading(true);
    setError("");
    try {
      const me = await login({ email: form.email.trim(), password: form.password });
      const from = location.state?.from;
      const home = ROLE_HOME[me.role] || "/";
      navigate(from && from.startsWith("/dashboard") ? from : home, { replace: true });
    } catch (err) {
      setError(err.friendlyMessage || "Invalid email or password");
    } finally {
      setLoading(false);
    }
  };

  return (
    <motion.div
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      exit={{ opacity: 0 }}
      transition={{ duration: 0.35 }}
      className="relative flex min-h-screen items-center justify-center overflow-hidden bg-page-glow px-5 py-12"
    >
      <div className="pointer-events-none absolute inset-0 bg-grid-pattern bg-grid [mask-image:radial-gradient(ellipse_60%_60%_at_50%_40%,black,transparent)]" />

      <div className="absolute right-5 top-5">
        <ThemeToggle />
      </div>

      <motion.div
        initial={{ opacity: 0, y: 28, scale: 0.98 }}
        animate={{ opacity: 1, y: 0, scale: 1 }}
        transition={{ duration: 0.55, ease: [0.21, 0.47, 0.32, 0.98] }}
        className="glass-card relative w-full max-w-md p-8 shadow-glow sm:p-10"
      >
        <div className="flex justify-center">
          <Logo />
        </div>

        <h1 className="mt-7 text-center font-display text-2xl font-semibold text-slate-900 dark:text-white">
          Welcome back
        </h1>
        <p className="mt-2 text-center text-sm text-slate-500 dark:text-slate-400">
          Sign in to continue your placement journey
        </p>

        {error && (
          <motion.div
            initial={{ opacity: 0, y: -8 }}
            animate={{ opacity: 1, y: 0 }}
            className="mt-6 flex items-start gap-2.5 rounded-xl border border-rose-500/30 bg-rose-500/10 px-4 py-3 text-sm text-rose-300"
            role="alert"
          >
            <FiAlertCircle className="mt-0.5 shrink-0" size={16} />
            <span>{error}</span>
          </motion.div>
        )}

        <form onSubmit={handleSubmit} className="mt-6 space-y-4">
          <div className="relative">
            <FiMail className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-500" size={16} />
            <input
              type="email"
              name="email"
              required
              autoComplete="email"
              value={form.email}
              onChange={handleChange}
              placeholder="you@college.edu"
              className="input-glass"
            />
          </div>

          <div className="relative">
            <FiLock className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-500" size={16} />
            <input
              type={showPassword ? "text" : "password"}
              name="password"
              required
              autoComplete="current-password"
              value={form.password}
              onChange={handleChange}
              placeholder="Your password"
              className="input-glass pr-12"
            />
            <button
              type="button"
              aria-label={showPassword ? "Hide password" : "Show password"}
              onClick={() => setShowPassword((visible) => !visible)}
              className="absolute right-4 top-1/2 -translate-y-1/2 text-slate-500 transition-colors hover:text-slate-700 dark:hover:text-slate-300"
            >
              {showPassword ? <FiEyeOff size={16} /> : <FiEye size={16} />}
            </button>
          </div>

          <GradientButton type="submit" disabled={loading} className="w-full">
            {loading ? (
              <>
                <FiLoader className="animate-spin" size={16} /> Signing in…
              </>
            ) : (
              <>
                Sign in <FiArrowRight size={16} />
              </>
            )}
          </GradientButton>
        </form>

        <p className="mt-7 text-center text-sm text-slate-500 dark:text-slate-400">
          New to PlaceNextAI?{" "}
          <Link to="/register" className="font-semibold text-primary-400 transition-colors hover:text-primary-500">
            Create an account
          </Link>
        </p>
      </motion.div>
    </motion.div>
  );
}
