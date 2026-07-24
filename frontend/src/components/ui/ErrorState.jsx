import { FiAlertTriangle, FiRefreshCw } from "react-icons/fi";
import GradientButton from "./GradientButton.jsx";

export default function ErrorState({ message = "Something went wrong.", onRetry }) {
  return (
    <div className="flex flex-col items-center justify-center px-6 py-16 text-center">
      <span className="flex h-14 w-14 items-center justify-center rounded-2xl border border-rose-500/30 bg-rose-500/10 text-rose-400">
        <FiAlertTriangle size={22} />
      </span>
      <h3 className="mt-4 font-display text-base font-semibold text-slate-900 dark:text-white">Could not load data</h3>
      <p className="mt-1.5 max-w-sm text-sm text-slate-500">{message}</p>
      {onRetry && (
        <div className="mt-5">
          <GradientButton variant="ghost" onClick={onRetry} className="!px-4 !py-2 text-xs">
            <FiRefreshCw size={14} /> Try again
          </GradientButton>
        </div>
      )}
    </div>
  );
}
