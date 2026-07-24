import { motion } from "framer-motion";

const RISK_STYLES = {
  LOW: { from: "#34d399", to: "#10b981", label: "Low risk", badge: "bg-emerald-500/10 text-emerald-400" },
  MEDIUM: { from: "#fbbf24", to: "#f59e0b", label: "Medium risk", badge: "bg-amber-500/10 text-amber-400" },
  HIGH: { from: "#fb7185", to: "#e11d48", label: "High risk", badge: "bg-rose-500/10 text-rose-400" },
};

export default function ProbabilityGauge({ probabilityScore, riskLevel }) {
  const style = RISK_STYLES[riskLevel] || RISK_STYLES.MEDIUM;
  const gradientId = "prediction-ring-" + riskLevel;

  return (
    <div className="flex flex-col items-center gap-3">
      <div className="relative flex h-36 w-36 items-center justify-center">
        <svg className="absolute inset-0 -rotate-90" viewBox="0 0 144 144">
          <circle cx="72" cy="72" r="62" fill="none" stroke="rgba(255,255,255,0.06)" strokeWidth="10" />
          <motion.circle
            cx="72"
            cy="72"
            r="62"
            fill="none"
            stroke={"url(#" + gradientId + ")"}
            strokeWidth="10"
            strokeLinecap="round"
            strokeDasharray={2 * Math.PI * 62}
            initial={{ strokeDashoffset: 2 * Math.PI * 62 }}
            animate={{
              strokeDashoffset: 2 * Math.PI * 62 * (1 - probabilityScore / 100),
            }}
            transition={{ duration: 1.2, ease: "easeOut" }}
          />
          <defs>
            <linearGradient id={gradientId} x1="0" y1="0" x2="1" y2="1">
              <stop offset="0%" stopColor={style.from} />
              <stop offset="100%" stopColor={style.to} />
            </linearGradient>
          </defs>
        </svg>
        <div className="text-center">
          <p className="font-display text-4xl font-semibold text-slate-900 dark:text-white">{probabilityScore}</p>
          <p className="text-xs text-slate-500">/ 100</p>
        </div>
      </div>
      <span className={"rounded-full px-3 py-1 text-xs font-semibold " + style.badge}>{style.label}</span>
    </div>
  );
}
