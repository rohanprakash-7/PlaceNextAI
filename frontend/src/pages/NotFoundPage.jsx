import { motion } from "framer-motion";
import { FiHome, FiArrowLeft } from "react-icons/fi";
import { useNavigate } from "react-router-dom";
import GradientButton from "../components/ui/GradientButton.jsx";

export default function NotFoundPage() {
  const navigate = useNavigate();

  return (
    <motion.div
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      exit={{ opacity: 0 }}
      transition={{ duration: 0.35 }}
      className="relative flex min-h-screen flex-col items-center justify-center overflow-hidden bg-page-glow px-5 text-center"
    >
      <div className="pointer-events-none absolute inset-0 bg-grid-pattern bg-grid [mask-image:radial-gradient(ellipse_60%_60%_at_50%_45%,black,transparent)]" />

      <motion.p
        initial={{ opacity: 0, scale: 0.85 }}
        animate={{ opacity: 1, scale: 1 }}
        transition={{ duration: 0.6, ease: [0.21, 0.47, 0.32, 0.98] }}
        className="relative animate-float font-display text-[7rem] font-bold leading-none text-gradient sm:text-[10rem]"
      >
        404
      </motion.p>

      <motion.h1
        initial={{ opacity: 0, y: 18 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.55, delay: 0.15 }}
        className="relative mt-4 font-display text-2xl font-semibold text-white sm:text-3xl"
      >
        This page went off the career map
      </motion.h1>

      <motion.p
        initial={{ opacity: 0, y: 18 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.55, delay: 0.25 }}
        className="relative mt-3 max-w-md text-sm leading-relaxed text-slate-400"
      >
        The page you are looking for doesn't exist or has been moved. Let's get you back on the
        path to placement success.
      </motion.p>

      <motion.div
        initial={{ opacity: 0, y: 18 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.55, delay: 0.35 }}
        className="relative mt-8 flex flex-col items-center gap-3 sm:flex-row"
      >
        <GradientButton to="/">
          <FiHome size={16} /> Back to home
        </GradientButton>
        <GradientButton variant="ghost" onClick={() => navigate(-1)}>
          <FiArrowLeft size={16} /> Go back
        </GradientButton>
      </motion.div>
    </motion.div>
  );
}
