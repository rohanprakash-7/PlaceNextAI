import { motion } from "framer-motion";
import { FiCheck, FiX } from "react-icons/fi";

export default function TimelineStages({ timeline }) {
  if (timeline.rejected) {
    return (
      <div className="flex items-center gap-3 rounded-xl border border-rose-500/30 bg-rose-500/10 px-4 py-3">
        <span className="flex h-8 w-8 shrink-0 items-center justify-center rounded-full bg-rose-500 text-white">
          <FiX size={15} />
        </span>
        <div>
          <p className="text-sm font-medium text-rose-300">Not selected for this role</p>
          <p className="mt-0.5 text-xs text-slate-500">
            {timeline.feedbackCount > 0
              ? `Check the recruiter feedback below for details.`
              : "No feedback shared yet."}
          </p>
        </div>
      </div>
    );
  }

  return (
    <div className="relative">
      <div className="absolute left-4 top-4 bottom-4 w-0.5 bg-white/10" />
      <div className="space-y-4">
        {timeline.stages.map((stage, index) => (
          <motion.div
            key={stage.status}
            initial={{ opacity: 0, x: -8 }}
            animate={{ opacity: 1, x: 0 }}
            transition={{ duration: 0.3, delay: index * 0.06 }}
            className="relative flex items-center gap-3 pl-0"
          >
            <span
              className={
                "z-10 flex h-8 w-8 shrink-0 items-center justify-center rounded-full border-2 transition-colors " +
                (stage.reached
                  ? "border-primary-500 bg-primary-500 text-white"
                  : "border-white/15 bg-night-900 text-transparent")
              }
            >
              {stage.reached && <FiCheck size={14} />}
            </span>
            <span
              className={
                "text-sm font-medium " +
                (stage.current ? "text-white" : stage.reached ? "text-slate-300" : "text-slate-600")
              }
            >
              {stage.label}
              {stage.current && (
                <span className="ml-2 rounded-full bg-primary-500/15 px-2 py-0.5 text-[10px] font-semibold uppercase tracking-wider text-primary-400">
                  Current
                </span>
              )}
            </span>
          </motion.div>
        ))}
      </div>
    </div>
  );
}
