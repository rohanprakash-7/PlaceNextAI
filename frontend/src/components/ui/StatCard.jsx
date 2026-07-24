import { motion } from "framer-motion";
import { FiArrowUpRight, FiArrowDownRight } from "react-icons/fi";

export default function StatCard({ icon: Icon, label, value, trend, trendUp = true, delay = 0 }) {
  return (
    <motion.div
      initial={{ opacity: 0, y: 18 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.5, delay, ease: [0.21, 0.47, 0.32, 0.98] }}
      whileHover={{ y: -4, borderColor: "rgba(139, 92, 246, 0.35)" }}
      className="glass-card p-5"
    >
      <div className="flex items-start justify-between">
        <span className="flex h-10 w-10 items-center justify-center rounded-xl bg-slate-900/[0.06] text-primary-600 dark:bg-white/[0.06] dark:text-primary-400">
          <Icon size={18} />
        </span>
        {trend && (
          <span
            className={
              "inline-flex items-center gap-1 rounded-full px-2 py-1 text-xs font-semibold " +
              (trendUp ? "bg-emerald-500/10 text-emerald-400" : "bg-rose-500/10 text-rose-400")
            }
          >
            {trendUp ? <FiArrowUpRight size={13} /> : <FiArrowDownRight size={13} />}
            {trend}
          </span>
        )}
      </div>
      <p className="mt-4 font-display text-2xl font-semibold text-slate-900 dark:text-white">{value}</p>
      <p className="mt-1 text-sm text-slate-500 dark:text-slate-400">{label}</p>
    </motion.div>
  );
}
