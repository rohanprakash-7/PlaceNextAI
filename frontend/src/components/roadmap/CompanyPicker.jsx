import { FiTarget } from "react-icons/fi";
import GradientButton from "../ui/GradientButton.jsx";

export default function CompanyPicker({ companies, selected, onSelect, onAnalyze, loading }) {
  return (
    <div className="glass-card p-6">
      <div className="flex items-center gap-2">
        <FiTarget className="text-primary-400" size={16} />
        <h2 className="font-display text-lg font-semibold text-slate-900 dark:text-white">Target a company</h2>
      </div>
      <p className="mt-1 text-xs text-slate-500">
        Pick a company to compare your skills against its open roles, or check readiness across all postings.
      </p>

      <div className="mt-4 flex flex-wrap gap-2">
        <button
          type="button"
          onClick={() => onSelect("")}
          className={
            "rounded-full px-3.5 py-1.5 text-xs font-medium transition-colors " +
            (selected === "" ? "bg-brand-gradient text-slate-900 dark:text-white" : "glass text-slate-500 dark:text-slate-400 hover:text-slate-900 dark:hover:text-white")
          }
        >
          All companies
        </button>
        {companies.map((company) => (
          <button
            key={company}
            type="button"
            onClick={() => onSelect(company)}
            className={
              "rounded-full px-3.5 py-1.5 text-xs font-medium transition-colors " +
              (selected === company ? "bg-brand-gradient text-slate-900 dark:text-white" : "glass text-slate-500 dark:text-slate-400 hover:text-slate-900 dark:hover:text-white")
            }
          >
            {company}
          </button>
        ))}
      </div>

      <div className="mt-5">
        <GradientButton onClick={onAnalyze} disabled={loading} className="w-full !py-2.5 text-sm">
          {loading ? "Analyzing…" : "Analyze skill gap"}
        </GradientButton>
      </div>
    </div>
  );
}
