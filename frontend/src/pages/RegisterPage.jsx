import { useState } from "react";
import { motion } from "framer-motion";
import { Link, Navigate, useNavigate } from "react-router-dom";
import {
  FiUser,
  FiMail,
  FiLock,
  FiEye,
  FiEyeOff,
  FiArrowRight,
  FiLoader,
  FiBookOpen,
  FiBriefcase,
  FiAward,
  FiAlertCircle,
} from "react-icons/fi";
import Logo from "../components/ui/Logo.jsx";
import GradientButton from "../components/ui/GradientButton.jsx";
import ThemeToggle from "../components/ui/ThemeToggle.jsx";
import { useAuth, ROLE_HOME } from "../context/AuthContext.jsx";

const ROLES = [
  { id: "STUDENT", label: "Student", icon: FiBookOpen },
  { id: "RECRUITER", label: "Recruiter", icon: FiBriefcase },
  { id: "ALUMNI", label: "Alumni", icon: FiAward },
];

export default function RegisterPage() {
  const navigate = useNavigate();
  const { user, initializing, registerStudent, registerRecruiter, registerAlumni } = useAuth();

  const [form, setForm] = useState({ fullName: "", email: "", password: "", companyName: "", currentCompany: "" });
  const [role, setRole] = useState("STUDENT");
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

  const extractError = (err) => {
    if (err.fieldErrors) {
      return Object.values(err.fieldErrors).join(" ");
    }
    return err.friendlyMessage || "Registration failed. Please try again.";
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setLoading(true);
    setError("");
    try {
      let me;
      if (role === "RECRUITER") {
        me = await registerRecruiter({
          companyName: form.companyName.trim(),
          recruiterName: form.fullName.trim(),
          email: form.email.trim(),
          password: form.password,
        });
      } else if (role === "ALUMNI") {
        me = await registerAlumni({
          fullName: form.fullName.trim(),
          email: form.email.trim(),
          password: form.password,
          currentCompany: form.currentCompany.trim(),
        });
      } else {
        me = await registerStudent({
          fullName: form.fullName.trim(),
          email: form.email.trim(),
          password: form.password,
        });
      }
      navigate(ROLE_HOME[me.role] || "/", { replace: true });
    } catch (err) {
      setError(extractError(err));
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
          Create your account
        </h1>
        <p className="mt-2 text-center text-sm text-slate-500 dark:text-slate-400">
          Start your AI-powered placement journey
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

        <div className="mt-6 grid grid-cols-3 gap-3">
          {ROLES.map((option) => {
            const Icon = option.icon;
            const selected = role === option.id;
            return (
              <button
                key={option.id}
                type="button"
                onClick={() => setRole(option.id)}
                className={
                  "flex flex-col items-center gap-2 rounded-xl border py-4 text-sm font-medium transition-all duration-200 " +
                  (selected
                    ? "border-primary-500/60 bg-primary-500/10 text-slate-900 dark:text-white shadow-glow-sm"
                    : "border-slate-200 dark:border-white/10 bg-slate-50 dark:bg-white/[0.03] text-slate-500 dark:text-slate-400 hover:border-slate-300 dark:hover:border-white/20 hover:text-slate-700 dark:hover:text-slate-200")
                }
              >
                <Icon size={18} className={selected ? "text-primary-400" : ""} />
                {option.label}
              </button>
            );
          })}
        </div>

        <form onSubmit={handleSubmit} className="mt-5 space-y-4">
          <div className="relative">
            <FiUser className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-500" size={16} />
            <input
              type="text"
              name="fullName"
              required
              autoComplete="name"
              value={form.fullName}
              onChange={handleChange}
              placeholder="Full name"
              className="input-glass"
            />
          </div>

          {role === "RECRUITER" && (
            <div className="relative">
              <FiBriefcase className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-500" size={16} />
              <input
                type="text"
                name="companyName"
                required
                value={form.companyName}
                onChange={handleChange}
                placeholder="Company name"
                className="input-glass"
              />
            </div>
          )}

          {role === "ALUMNI" && (
            <div className="relative">
              <FiAward className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-500" size={16} />
              <input
                type="text"
                name="currentCompany"
                value={form.currentCompany}
                onChange={handleChange}
                placeholder="Current company (optional)"
                className="input-glass"
              />
            </div>
          )}

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
              minLength={8}
              autoComplete="new-password"
              value={form.password}
              onChange={handleChange}
              placeholder="Create a password (8+ characters)"
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
                <FiLoader className="animate-spin" size={16} /> Creating account…
              </>
            ) : (
              <>
                Create account <FiArrowRight size={16} />
              </>
            )}
          </GradientButton>
        </form>

        <p className="mt-6 text-center text-xs leading-relaxed text-slate-500">
          By creating an account you agree to our Terms of Service and Privacy Policy.
        </p>

        <p className="mt-5 text-center text-sm text-slate-500 dark:text-slate-400">
          Already have an account?{" "}
          <Link to="/login" className="font-semibold text-primary-400 transition-colors hover:text-primary-500">
            Sign in
          </Link>
        </p>
      </motion.div>
    </motion.div>
  );
}
