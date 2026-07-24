import { motion } from "framer-motion";
import { FiCheckCircle, FiXCircle } from "react-icons/fi";
import GradientButton from "../ui/GradientButton.jsx";

export default function SkillGapPanel({ gap, onGenerate, generating }) {
  return (
    <div className="glass-card p-6">
      <div className="flex flex-wrap items-center justify-between gap-3">
        <div>
          <h2 className="font-display text-lg font-semibold text-slate-900 dark:text-white">{gap.targetCompany}</h2>
          <p className="mt-1 text-xs text-slate-500">
            {gap.requiredSkills.length} skills required across their postings
          </p>
        </div>
        <div className="text-right">
          <p className="font-display text-3xl font-semibold text-gradient">{gap.coveragePercent}%</p>
          <p className="text-xs text-slate-500">skill coverage</p>
        </div>
      </div>

      <div className="mt-2 h-2 overflow-hidden rounded-full bg-white/5">
        <motion.div
          initial={{ width: 0 }}
          animate={{ width: gap.coveragePercent + "%" }}
          transition={{ duration: 0.9, ease: "easeOut" }}
          className="h-full rounded-full bg-brand-gradient"
        />
      </div>

      <div className="mt-6 grid gap-5 sm:grid-cols-2">
        <div>
          <p className="flex items-center gap-1.5 text-xs uppercase tracking-wider text-slate-500">
            <FiCheckCircle className="text-emerald-400" size={13} /> You have
          </p>
          <div className="mt-2 flex flex-wrap gap-2">
            {gap.currentSkills.length === 0 && (
              <span className="text-sm text-slate-500">No skills on your profile yet.</span>
            )}
            {gap.currentSkills.map((skill) => (
              <span key={skill} className="rounded-full bg-emerald-500/10 px-3 py-1 text-xs text-emerald-300">
                {skill}
              </span>
            ))}
          </div>
        </div>

        <div>
          <p className="flex items-center gap-1.5 text-xs uppercase tracking-wider text-slate-500">
            <FiXCircle className="text-amber-400" size={13} /> Missing
          </p>
          <div className="mt-2 flex flex-wrap gap-2">
            {gap.missingSkills.length === 0 && (
              <span className="text-sm text-emerald-400">Full coverage — you're ready!</span>
            )}
            {gap.missingSkills.map((skill) => (
              <span key={skill} className="rounded-full bg-amber-500/10 px-3 py-1 text-xs text-amber-300">
                {skill}
              </span>
            ))}
          </div>
        </div>
      </div>

      {gap.missingSkills.length > 0 && (
        <div className="mt-6">
          <GradientButton onClick={onGenerate} disabled={generating} className="w-full">
            {generating ? "Generating roadmap…" : "Generate my learning roadmap"}
          </GradientButton>
        </div>
      )}
    </div>
  );
}
