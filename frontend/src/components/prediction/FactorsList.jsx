import { FiArrowUpRight, FiArrowDownRight } from "react-icons/fi";

function FactorRow({ factor, positive }) {
  return (
    <div
      className={
        "flex items-start gap-2.5 rounded-xl border px-3.5 py-3 text-sm " +
        (positive
          ? "border-emerald-500/20 bg-emerald-500/5"
          : "border-rose-500/20 bg-rose-500/5")
      }
    >
      {positive ? (
        <FiArrowUpRight className="mt-0.5 shrink-0 text-emerald-400" size={15} />
      ) : (
        <FiArrowDownRight className="mt-0.5 shrink-0 text-rose-400" size={15} />
      )}
      <div>
        <p className={"font-medium " + (positive ? "text-emerald-300" : "text-rose-300")}>
          {factor.label}
          <span className="ml-2 text-xs font-normal text-slate-500">{factor.impact}</span>
        </p>
        <p className="mt-0.5 text-xs text-slate-400">{factor.description}</p>
      </div>
    </div>
  );
}

export default function FactorsList({ positiveFactors = [], negativeFactors = [] }) {
  if (positiveFactors.length === 0 && negativeFactors.length === 0) {
    return <p className="text-sm text-slate-500">Not enough data yet to explain this score.</p>;
  }

  return (
    <div className="space-y-2.5">
      {positiveFactors.map((factor) => (
        <FactorRow key={"pos-" + factor.label} factor={factor} positive />
      ))}
      {negativeFactors.map((factor) => (
        <FactorRow key={"neg-" + factor.label} factor={factor} positive={false} />
      ))}
    </div>
  );
}
