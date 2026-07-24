import { motion } from "framer-motion";
import { Link } from "react-router-dom";

export default function GradientButton({
  children,
  to,
  variant = "primary",
  className = "",
  type = "button",
  disabled = false,
  onClick,
}) {
  const base =
    "inline-flex items-center justify-center gap-2 rounded-xl px-6 py-3 text-sm font-semibold transition-colors duration-200 disabled:cursor-not-allowed disabled:opacity-60";

  const variants = {
    primary: "bg-brand-gradient text-white shadow-glow-sm hover:shadow-glow",
    ghost:
      "glass text-slate-700 hover:border-primary-500/40 hover:bg-slate-900/5 hover:text-slate-900 dark:text-slate-200 dark:hover:bg-white/[0.07] dark:hover:text-white",
  };

  const classes = base + " " + variants[variant] + " " + className;

  const motionProps = {
    whileHover: disabled ? undefined : { scale: 1.03 },
    whileTap: disabled ? undefined : { scale: 0.97 },
    transition: { type: "spring", stiffness: 400, damping: 22 },
  };

  if (to) {
    return (
      <motion.span {...motionProps} className="inline-flex">
        <Link to={to} className={classes}>
          {children}
        </Link>
      </motion.span>
    );
  }

  return (
    <motion.button
      {...motionProps}
      type={type}
      disabled={disabled}
      onClick={onClick}
      className={classes}
    >
      {children}
    </motion.button>
  );
}
