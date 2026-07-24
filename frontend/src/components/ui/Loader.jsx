import { motion } from "framer-motion";

export default function Loader({ fullScreen = true, label = "PlaceNextAI" }) {
  const wrapper = fullScreen
    ? "fixed inset-0 z-[100] flex flex-col items-center justify-center gap-6 bg-slate-50 dark:bg-night-950"
    : "flex flex-col items-center justify-center gap-6 py-20";

  return (
    <div className={wrapper}>
      <div className="relative h-16 w-16">
        <motion.span
          className="absolute inset-0 rounded-full border-2 border-transparent border-t-primary-500 border-r-accent-500"
          animate={{ rotate: 360 }}
          transition={{ duration: 1, repeat: Infinity, ease: "linear" }}
        />
        <motion.span
          className="absolute inset-2 rounded-full border-2 border-transparent border-b-accent-400 border-l-primary-400"
          animate={{ rotate: -360 }}
          transition={{ duration: 1.4, repeat: Infinity, ease: "linear" }}
        />
        <motion.span
          className="absolute inset-[26px] rounded-full bg-brand-gradient shadow-glow-sm"
          animate={{ scale: [1, 1.25, 1] }}
          transition={{ duration: 1.2, repeat: Infinity, ease: "easeInOut" }}
        />
      </div>
      <motion.p
        className="font-display text-sm font-medium tracking-[0.3em] text-slate-400"
        animate={{ opacity: [0.4, 1, 0.4] }}
        transition={{ duration: 1.6, repeat: Infinity, ease: "easeInOut" }}
      >
        {label.toUpperCase()}
      </motion.p>
    </div>
  );
}
