export default function CandidateComparisonTable({ candidates }) {
  if (!candidates || candidates.length === 0) {
    return null;
  }

  const rows = [
    { key: "rankScore", label: "Rank score" },
    { key: "skillMatchPercent", label: "Skill match %" },
    { key: "readinessScore", label: "Readiness" },
    { key: "predictionScore", label: "Placement probability" },
    { key: "interviewSignal", label: "Interview signal" },
  ];

  return (
    <div className="glass-card overflow-hidden">
      <div className="border-b border-slate-200 dark:border-white/5 px-6 py-5">
        <h2 className="font-display text-lg font-semibold text-slate-900 dark:text-white">Side-by-side comparison</h2>
      </div>
      <div className="overflow-x-auto">
        <table className="w-full min-w-[560px] text-left text-sm">
          <thead>
            <tr className="border-b border-slate-200 dark:border-white/5 text-xs uppercase tracking-wider text-slate-500">
              <th className="px-6 py-3.5 font-medium">Signal</th>
              {candidates.map((candidate) => (
                <th key={candidate.studentId} className="px-6 py-3.5 font-medium text-slate-700 dark:text-slate-300">
                  {candidate.studentName}
                </th>
              ))}
            </tr>
          </thead>
          <tbody>
            {rows.map((row) => (
              <tr key={row.key} className="border-b border-slate-200 dark:border-white/5 last:border-0">
                <td className="px-6 py-3.5 text-slate-500 dark:text-slate-400">{row.label}</td>
                {candidates.map((candidate) => (
                  <td key={candidate.studentId} className="px-6 py-3.5 font-semibold text-slate-900 dark:text-white">
                    {candidate[row.key]}
                  </td>
                ))}
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
