import { motion } from "framer-motion";
import { FiRefreshCw, FiZap } from "react-icons/fi";
import BreakdownList from "./BreakdownList.jsx";
import ScoreSparkline from "./ScoreSparkline.jsx";

export default function ReadinessCard({ readiness, onRecompute, recomputing }) {
  return (
    <div className="glass-card p-6">
      <div className="flex flex-wrap items-start justify-between gap-4">
        <div>
          <div className="flex items-center gap-2">
            <FiZap className="text-primary-400" size={16} />
            <h2 className="font-display text-lg font-semibold text-slate-900 dark:text-white">Placement Readiness Score</h2>
          </div>
          <p className="mt-1 text-xs text-slate-500">
            Recomputed automatically on every action · last updated{" "}
            {new Date(readiness.computedAt).toLocaleString()}
          </p>
        </div>
        <button
          type="button"
          onClick={onRecompute}
          disabled={recomputing}
          className="glass inline-flex items-center gap-2 rounded-xl px-3.5 py-2 text-xs font-medium text-slate-700 dark:text-slate-300 transition-colors hover:text-slate-900 dark:hover:text-white disabled:opacity-50"
        >
          <FiRefreshCw size={13} className={recomputing ? "animate-spin" : ""} />
          {recomputing ? "Recomputing…" : "Refresh"}
        </button>
      </div>

      <div className="mt-6 flex flex-col gap-8 lg:flex-row lg:items-center">
        <div className="flex flex-col items-center gap-3">
          <div className="relative flex h-36 w-36 items-center justify-center">
            <svg className="absolute inset-0 -rotate-90" viewBox="0 0 144 144">
              <circle cx="72" cy="72" r="62" fill="none" stroke="rgba(255,255,255,0.06)" strokeWidth="10" />
              <motion.circle
                cx="72"
                cy="72"
                r="62"
                fill="none"
                stroke="url(#ring-gradient)"
                strokeWidth="10"
                strokeLinecap="round"
                strokeDasharray={2 * Math.PI * 62}
                initial={{ strokeDashoffset: 2 * Math.PI * 62 }}
                animate={{
                  strokeDashoffset: 2 * Math.PI * 62 * (1 - readiness.totalScore / 100),
                }}
                transition={{ duration: 1.2, ease: "easeOut" }}
              />
              <defs>
                <linearGradient id="ring-gradient" x1="0" y1="0" x2="1" y2="1">
                  <stop offset="0%" stopColor="#8b5cf6" />
                  <stop offset="100%" stopColor="#3b82f6" />
                </linearGradient>
              </defs>
            </svg>
            <div className="text-center">
              <p className="font-display text-4xl font-semibold text-slate-900 dark:text-white">{readiness.totalScore}</p>
              <p className="text-xs text-slate-500">/ 100</p>
            </div>
          </div>
          <ScoreSparkline history={readiness.history} />
        </div>

        <div className="flex-1">
          <BreakdownList readiness={readiness} />
          {readiness.improvementTip && (
            <div className="mt-5 rounded-xl border border-primary-500/20 bg-primary-500/5 px-4 py-3 text-sm text-slate-700 dark:text-slate-300">
              <span className="font-semibold text-primary-400">Next step: </span>
              {readiness.improvementTip}
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
