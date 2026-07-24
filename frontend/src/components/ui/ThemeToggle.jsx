import { motion, AnimatePresence } from "framer-motion";
import { FiSun, FiMoon } from "react-icons/fi";
import { useTheme } from "../../context/ThemeContext.jsx";

export default function ThemeToggle({ className = "" }) {
  const { isDark, toggleTheme } = useTheme();

  return (
    <button
      type="button"
      onClick={toggleTheme}
      aria-label={isDark ? "Switch to day mode" : "Switch to night mode"}
      title={isDark ? "Switch to day mode" : "Switch to night mode"}
      className={
        "glass flex h-10 w-10 items-center justify-center overflow-hidden rounded-xl text-slate-500 transition-colors hover:text-slate-900 dark:text-slate-300 dark:hover:text-white " +
        className
      }
    >
      <AnimatePresence mode="wait" initial={false}>
        <motion.span
          key={isDark ? "moon" : "sun"}
          initial={{ y: -14, opacity: 0, rotate: -40 }}
          animate={{ y: 0, opacity: 1, rotate: 0 }}
          exit={{ y: 14, opacity: 0, rotate: 40 }}
          transition={{ duration: 0.22, ease: [0.21, 0.47, 0.32, 0.98] }}
          className="flex items-center justify-center"
        >
          {isDark ? <FiMoon size={17} /> : <FiSun size={17} />}
        </motion.span>
      </AnimatePresence>
    </button>
  );
}
