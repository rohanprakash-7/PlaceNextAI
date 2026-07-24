import { motion } from "framer-motion";

export default function BreakdownList({ readiness }) {
  const dimensions = [
    { label: "Academic", value: readiness.academicScore },
    { label: "Resume", value: readiness.resumeScore },
    { label: "Skills", value: readiness.skillScore },
    { label: "Interview", value: readiness.interviewScore },
    { label: "Activity", value: readiness.activityScore },
  ];

  return (
    <div className="space-y-4">
      {dimensions.map((dimension, index) => {
        const weakest = dimension.label === readiness.weakestDimension;
        return (
          <div key={dimension.label}>
            <div className="flex items-center justify-between text-sm">
              <span className={weakest ? "font-medium text-amber-300" : "text-slate-700 dark:text-slate-300"}>
                {dimension.label}
                {weakest && (
                  <span className="ml-2 rounded-full bg-amber-500/10 px-2 py-0.5 text-[10px] font-semibold uppercase tracking-wider text-amber-400">
                    Focus here
                  </span>
                )}
              </span>
              <span className="font-semibold text-slate-900 dark:text-white">{dimension.value}</span>
            </div>
            <div className="mt-2 h-2 overflow-hidden rounded-full bg-white/5">
              <motion.div
                initial={{ width: 0 }}
                animate={{ width: dimension.value + "%" }}
                transition={{ duration: 0.9, delay: 0.2 + index * 0.1, ease: "easeOut" }}
                className={
                  "h-full rounded-full " +
                  (weakest ? "bg-gradient-to-r from-amber-500 to-orange-500" : "bg-brand-gradient")
                }
              />
            </div>
          </div>
        );
      })}
    </div>
  );
}
