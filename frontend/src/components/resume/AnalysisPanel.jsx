import { motion } from "framer-motion";
import { FiCheckCircle, FiAlertCircle, FiTrendingUp } from "react-icons/fi";

export default function AnalysisPanel({ version }) {
  return (
    <div className="glass-card p-6">
      <div className="flex flex-wrap items-center justify-between gap-3">
        <div>
          <h2 className="font-display text-lg font-semibold text-white">
            Version {version.versionNumber} · {version.fileName}
          </h2>
          <p className="mt-1 text-xs text-slate-500">
            {new Date(version.createdAt).toLocaleString()}
            {version.wordCount ? ` · ${version.wordCount} words` : ""}
          </p>
        </div>
        <div className="text-right">
          <p className="font-display text-3xl font-semibold text-gradient">{version.atsScore}</p>
          <p className="text-xs text-slate-500">ATS score</p>
        </div>
      </div>

      <div className="mt-2 h-2 overflow-hidden rounded-full bg-white/5">
        <motion.div
          initial={{ width: 0 }}
          animate={{ width: version.atsScore + "%" }}
          transition={{ duration: 0.9, ease: "easeOut" }}
          className="h-full rounded-full bg-brand-gradient"
        />
      </div>

      {version.extractedSkills.length > 0 && (
        <div className="mt-6">
          <p className="flex items-center gap-1.5 text-xs uppercase tracking-wider text-slate-500">
            <FiCheckCircle className="text-emerald-400" size={13} /> Skills detected
          </p>
          <div className="mt-2 flex flex-wrap gap-2">
            {version.extractedSkills.map((skill) => (
              <span key={skill} className="rounded-full bg-emerald-500/10 px-3 py-1 text-xs text-emerald-300">
                {skill}
              </span>
            ))}
          </div>
        </div>
      )}

      {version.missingKeywords.length > 0 && (
        <div className="mt-5">
          <p className="flex items-center gap-1.5 text-xs uppercase tracking-wider text-slate-500">
            <FiAlertCircle className="text-amber-400" size={13} /> Missing from the job description
          </p>
          <div className="mt-2 flex flex-wrap gap-2">
            {version.missingKeywords.map((keyword) => (
              <span key={keyword} className="rounded-full bg-amber-500/10 px-3 py-1 text-xs text-amber-300">
                {keyword}
              </span>
            ))}
          </div>
        </div>
      )}

      {version.suggestions.length > 0 && (
        <div className="mt-5">
          <p className="flex items-center gap-1.5 text-xs uppercase tracking-wider text-slate-500">
            <FiTrendingUp className="text-primary-400" size={13} /> Suggestions
          </p>
          <ul className="mt-2 space-y-2">
            {version.suggestions.map((suggestion) => (
              <li key={suggestion} className="flex gap-2 text-sm leading-relaxed text-slate-300">
                <span className="mt-2 h-1 w-1 shrink-0 rounded-full bg-primary-400" />
                {suggestion}
              </li>
            ))}
          </ul>
        </div>
      )}
    </div>
  );
}
