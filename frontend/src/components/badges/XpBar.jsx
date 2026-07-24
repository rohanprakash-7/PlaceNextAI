import { motion } from "framer-motion";
import { FiZap } from "react-icons/fi";

export default function XpBar({ summary }) {
  if (!summary) {
    return null;
  }

  return (
    <div className="w-full sm:w-72">
      <div className="flex items-center justify-between text-sm">
        <span className="inline-flex items-center gap-1.5 font-semibold text-slate-900 dark:text-white">
          Level {summary.level}
        </span>
        <span className="text-slate-500 dark:text-slate-400">
          {summary.xpIntoLevel} / {summary.xpForNextLevel} XP
        </span>
      </div>
      <div className="mt-2 h-2 overflow-hidden rounded-full bg-white/5">
        <motion.div
          initial={{ width: 0 }}
          animate={{ width: (summary.progressPercent || 0) + "%" }}
          transition={{ duration: 1, delay: 0.2, ease: "easeOut" }}
          className="h-full rounded-full bg-brand-gradient"
        />
      </div>
      {summary.currentStreak > 0 && (
        <p className="mt-2 flex items-center gap-1.5 text-xs text-slate-500">
          <FiZap className="text-amber-400" size={12} />
          {summary.currentStreak}-day streak
          {summary.longestStreak > summary.currentStreak
            ? " · best " + summary.longestStreak + " days"
            : ""}
        </p>
      )}
    </div>
  );
}
