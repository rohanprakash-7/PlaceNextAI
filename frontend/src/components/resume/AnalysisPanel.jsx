import { Link } from "react-router-dom";
import { motion } from "framer-motion";
import { FiCheckCircle, FiAlertCircle, FiTrendingUp, FiAward } from "react-icons/fi";

export default function AnalysisPanel({ version }) {
  return (
    <div className="glass-card p-6">
      <div className="flex flex-wrap items-center justify-between gap-3">
        <div>
          <h2 className="font-display text-lg font-semibold text-slate-900 dark:text-white">
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

      {version.eligibleCompanies && version.eligibleCompanies.length > 0 && (
        <div className="mt-6 rounded-xl border border-emerald-500/30 bg-emerald-500/10 p-4">
          <p className="flex items-center gap-1.5 text-xs font-semibold uppercase tracking-wider text-emerald-500">
            <FiAward size={13} /> You're eligible for
          </p>
          <div className="mt-2 flex flex-wrap gap-2">
            {version.eligibleCompanies.map((item) => (
              <span
                key={item.company}
                className="inline-flex items-center gap-1.5 rounded-full bg-emerald-500/15 px-3 py-1 text-xs font-medium text-emerald-500"
                title={item.matchPercent + "% skill match"}
              >
                {item.company}
                <span className="rounded-full bg-emerald-500/25 px-1.5 py-0.5 text-[10px] font-semibold">
                  {item.successProbability}%
                </span>
              </span>
            ))}
          </div>
          <p className="mt-2 text-[11px] text-slate-500">
            % shown is an estimated placement chance from your skill match, CGPA fit, resume score and interview
            performance - not a guarantee.
          </p>
          <Link
            to="/dashboard/student/eligibility"
            className="mt-3 inline-block text-xs font-medium text-emerald-500 hover:text-emerald-400"
          >
            See full breakdown per role →
          </Link>
        </div>
      )}

      {version.eligibleCompanies && version.eligibleCompanies.length === 0 && (
        <div className="mt-6 rounded-xl border border-amber-500/30 bg-amber-500/10 p-4">
          <p className="flex items-center gap-1.5 text-xs font-semibold uppercase tracking-wider text-amber-500">
            <FiAlertCircle size={13} /> Not yet eligible for any listed company
          </p>
          <p className="mt-1 text-xs text-amber-600 dark:text-amber-400">
            Close your skill gaps to unlock eligibility.
          </p>
          <Link
            to="/dashboard/student/roadmap"
            className="mt-2 inline-block text-xs font-medium text-amber-500 hover:text-amber-400"
          >
            Build a roadmap →
          </Link>
        </div>
      )}

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
              <li key={suggestion} className="flex gap-2 text-sm leading-relaxed text-slate-700 dark:text-slate-300">
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
