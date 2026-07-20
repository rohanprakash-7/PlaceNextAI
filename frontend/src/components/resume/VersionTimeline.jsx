import { FiArrowUpRight, FiArrowDownRight, FiMinus } from "react-icons/fi";

export default function VersionTimeline({ versions, selectedId, onSelect }) {
  // versions arrive newest-first; compare each against the one before it
  return (
    <div className="glass-card p-6">
      <h2 className="font-display text-lg font-semibold text-white">Version history</h2>
      <p className="mt-1 text-xs text-slate-500">Watch your ATS score improve across versions.</p>

      <div className="mt-5 space-y-2">
        {versions.map((version, index) => {
          const previous = versions[index + 1];
          const delta = previous ? version.atsScore - previous.atsScore : null;
          const previousSkills = new Set(previous?.extractedSkills || []);
          const added = previous
            ? version.extractedSkills.filter((skill) => !previousSkills.has(skill))
            : [];
          const selected = version.id === selectedId;

          return (
            <button
              key={version.id}
              type="button"
              onClick={() => onSelect(version)}
              className={
                "w-full rounded-xl border px-4 py-3.5 text-left transition-all " +
                (selected
                  ? "border-primary-500/50 bg-primary-500/10"
                  : "border-white/5 bg-white/[0.02] hover:border-white/15")
              }
            >
              <div className="flex items-center justify-between">
                <span className="text-sm font-medium text-white">
                  v{version.versionNumber}
                  <span className="ml-2 text-xs font-normal text-slate-500">
                    {new Date(version.createdAt).toLocaleDateString()}
                  </span>
                </span>
                <span className="flex items-center gap-2">
                  {delta !== null && (
                    <span
                      className={
                        "inline-flex items-center gap-0.5 rounded-full px-2 py-0.5 text-xs font-semibold " +
                        (delta > 0
                          ? "bg-emerald-500/10 text-emerald-400"
                          : delta < 0
                            ? "bg-rose-500/10 text-rose-400"
                            : "bg-slate-500/10 text-slate-400")
                      }
                    >
                      {delta > 0 ? <FiArrowUpRight size={11} /> : delta < 0 ? <FiArrowDownRight size={11} /> : <FiMinus size={11} />}
                      {delta > 0 ? "+" + delta : delta}
                    </span>
                  )}
                  <span className="font-display text-base font-semibold text-white">{version.atsScore}</span>
                </span>
              </div>
              {added.length > 0 && (
                <p className="mt-1.5 truncate text-xs text-emerald-400/80">
                  + added: {added.join(", ")}
                </p>
              )}
            </button>
          );
        })}
      </div>
    </div>
  );
}
