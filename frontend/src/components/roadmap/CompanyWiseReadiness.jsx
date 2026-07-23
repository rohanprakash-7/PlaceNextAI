import { useEffect, useState } from "react";
import { FiTarget } from "react-icons/fi";
import { SkeletonBlock } from "../ui/Skeleton.jsx";
import { getSkillGap, getTargetCompanies } from "../../services/roadmapService";

export default function CompanyWiseReadiness() {
  const [rows, setRows] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    let cancelled = false;

    async function load() {
      setLoading(true);
      try {
        const companies = await getTargetCompanies();
        const results = await Promise.all(
          companies.map(async (company) => {
            try {
              const gap = await getSkillGap(company);
              return { company, coverage: gap.coveragePercent };
            } catch {
              return { company, coverage: 0 };
            }
          })
        );
        if (!cancelled) {
          setRows(results.sort((a, b) => b.coverage - a.coverage));
        }
      } catch {
        if (!cancelled) setRows([]);
      } finally {
        if (!cancelled) setLoading(false);
      }
    }

    load();
    return () => {
      cancelled = true;
    };
  }, []);

  if (loading) {
    return <SkeletonBlock className="h-40" />;
  }

  if (rows.length === 0) {
    return null;
  }

  return (
    <div className="glass-card p-6">
      <div className="flex items-center gap-2">
        <FiTarget className="text-primary-400" size={16} />
        <h2 className="font-display text-lg font-semibold text-white">Readiness by target company</h2>
      </div>
      <p className="mt-1 text-xs text-slate-500">How much of each company's required skill set you already cover</p>

      <div className="mt-5 space-y-4">
        {rows.map((row) => (
          <div key={row.company}>
            <div className="flex items-center justify-between text-sm">
              <span className="text-slate-300">{row.company}</span>
              <span className="font-semibold text-white">{row.coverage}%</span>
            </div>
            <div className="mt-2 h-2 overflow-hidden rounded-full bg-white/5">
              <div className="h-full rounded-full bg-brand-gradient" style={{ width: row.coverage + "%" }} />
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
