import { motion } from "framer-motion";
import { FiMessageSquare, FiTrendingUp, FiTrendingDown, FiMinus } from "react-icons/fi";

const DIMENSIONS = [
  { key: "avgCommunication", label: "Communication" },
  { key: "avgTechnical", label: "Technical depth" },
  { key: "avgProblemSolving", label: "Problem solving" },
  { key: "avgCultureFit", label: "Culture fit" },
];

export default function FeedbackSummaryCard({ summary }) {
  if (summary.totalFeedbackCount === 0) {
    return (
      <div className="glass-card p-6">
        <div className="flex items-center gap-2">
          <FiMessageSquare className="text-primary-400" size={16} />
          <h2 className="font-display text-lg font-semibold text-slate-900 dark:text-white">Recruiter feedback</h2>
        </div>
        <p className="mt-3 text-sm text-slate-500">
          No feedback yet — once a recruiter reviews one of your applications, an anonymized
          summary will appear here and factor into your Readiness Score.
        </p>
      </div>
    );
  }

  const AdjustmentIcon = summary.scoreAdjustment > 0 ? FiTrendingUp : summary.scoreAdjustment < 0 ? FiTrendingDown : FiMinus;

  return (
    <div className="glass-card p-6">
      <div className="flex flex-wrap items-center justify-between gap-3">
        <div className="flex items-center gap-2">
          <FiMessageSquare className="text-primary-400" size={16} />
          <h2 className="font-display text-lg font-semibold text-slate-900 dark:text-white">Recruiter feedback</h2>
        </div>
        <span
          className={
            "inline-flex items-center gap-1.5 rounded-full px-3 py-1 text-xs font-semibold " +
            (summary.scoreAdjustment > 0
              ? "bg-emerald-500/10 text-emerald-400"
              : summary.scoreAdjustment < 0
                ? "bg-rose-500/10 text-rose-400"
                : "bg-slate-500/10 text-slate-500 dark:text-slate-400")
          }
        >
          <AdjustmentIcon size={12} />
          {summary.scoreAdjustment > 0 ? "+" : ""}
          {summary.scoreAdjustment} to readiness score
        </span>
      </div>
      <p className="mt-1 text-xs text-slate-500">
        Averaged across {summary.totalFeedbackCount} interview{summary.totalFeedbackCount > 1 ? "s" : ""}
      </p>

      <div className="mt-5 grid gap-4 sm:grid-cols-2">
        {DIMENSIONS.map((dimension, index) => (
          <div key={dimension.key}>
            <div className="flex items-center justify-between text-sm">
              <span className="text-slate-700 dark:text-slate-300">{dimension.label}</span>
              <span className="font-semibold text-slate-900 dark:text-white">{summary[dimension.key]} / 5</span>
            </div>
            <div className="mt-2 h-1.5 overflow-hidden rounded-full bg-white/5">
              <motion.div
                initial={{ width: 0 }}
                animate={{ width: (summary[dimension.key] / 5) * 100 + "%" }}
                transition={{ duration: 0.8, delay: index * 0.08, ease: "easeOut" }}
                className="h-full rounded-full bg-brand-gradient"
              />
            </div>
          </div>
        ))}
      </div>

      {summary.recentComments.length > 0 && (
        <div className="mt-5 space-y-2">
          <p className="text-xs uppercase tracking-wider text-slate-500">Recent notes</p>
          {summary.recentComments.map((comment, index) => (
            <p key={index} className="rounded-lg bg-slate-50 dark:bg-white/[0.03] px-3 py-2 text-sm text-slate-700 dark:text-slate-300">
              "{comment}"
            </p>
          ))}
        </div>
      )}
    </div>
  );
}
