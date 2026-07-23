import { motion } from "framer-motion";
import FactorsList from "../prediction/FactorsList.jsx";

function toFactors(candidate) {
  const signals = [
    { label: "Skill Match", value: candidate.skillMatchPercent, description: "Overlap with the job's required skills." },
    { label: "Readiness", value: candidate.readinessScore, description: "Current platform readiness score." },
    { label: "Placement Probability", value: candidate.predictionScore, description: "AI-estimated placement likelihood." },
    { label: "Interview Signal", value: candidate.interviewSignal, description: "Average recruiter feedback rating so far." },
  ];

  const positiveFactors = signals
    .filter((signal) => signal.value >= 65)
    .map((signal) => ({ label: signal.label, impact: signal.value, description: signal.description }));
  const negativeFactors = signals
    .filter((signal) => signal.value < 40)
    .map((signal) => ({ label: signal.label, impact: signal.value, description: signal.description }));

  return { positiveFactors, negativeFactors };
}

export default function RankedCandidateCard({ candidate, rank, selected, onToggleSelect }) {
  const { positiveFactors, negativeFactors } = toFactors(candidate);

  return (
    <motion.div
      initial={{ opacity: 0, y: 14 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.4, delay: rank * 0.06 }}
      className="glass-card p-5"
    >
      <div className="flex items-start justify-between gap-4">
        <div className="flex items-center gap-3">
          <span className="flex h-9 w-9 items-center justify-center rounded-xl bg-brand-gradient text-sm font-bold text-white">
            #{rank + 1}
          </span>
          <div>
            <p className="font-display text-base font-semibold text-white">{candidate.studentName}</p>
            <p className="text-xs text-slate-500">{candidate.email}</p>
          </div>
        </div>
        <div className="flex items-center gap-3">
          <div className="text-right">
            <p className="font-display text-xl font-semibold text-white">{candidate.rankScore}</p>
            <p className="text-[11px] uppercase tracking-wider text-slate-500">Rank score</p>
          </div>
          {onToggleSelect && (
            <input
              type="checkbox"
              checked={Boolean(selected)}
              onChange={() => onToggleSelect(candidate.studentId)}
              aria-label={"Select " + candidate.studentName + " for comparison"}
              className="h-4 w-4 rounded border-white/20 bg-white/5 text-primary-500"
            />
          )}
        </div>
      </div>

      {candidate.matchedSkills?.length > 0 && (
        <div className="mt-4 flex flex-wrap gap-1.5">
          {candidate.matchedSkills.map((skill) => (
            <span key={skill} className="rounded-full bg-emerald-500/10 px-2.5 py-1 text-[11px] font-medium text-emerald-400">
              {skill}
            </span>
          ))}
          {candidate.missingSkills.map((skill) => (
            <span key={skill} className="rounded-full bg-white/5 px-2.5 py-1 text-[11px] font-medium text-slate-500 line-through">
              {skill}
            </span>
          ))}
        </div>
      )}

      <div className="mt-4">
        <FactorsList positiveFactors={positiveFactors} negativeFactors={negativeFactors} />
      </div>
    </motion.div>
  );
}
