import { motion } from "framer-motion";
import { FiCheck, FiCircle, FiMic, FiBookOpen } from "react-icons/fi";

export default function RoadmapTimeline({ roadmap, onComplete, completingId }) {
  return (
    <div className="glass-card p-6">
      <div className="flex flex-wrap items-center justify-between gap-3">
        <div>
          <h2 className="font-display text-lg font-semibold text-white">
            Roadmap · {roadmap.targetCompany}
          </h2>
          <p className="mt-1 text-xs text-slate-500">
            {roadmap.completedItems} of {roadmap.totalItems} weeks completed
          </p>
        </div>
        <span className="font-display text-2xl font-semibold text-white">{roadmap.progressPercent}%</span>
      </div>

      <div className="mt-2 h-2 overflow-hidden rounded-full bg-white/5">
        <motion.div
          initial={{ width: 0 }}
          animate={{ width: roadmap.progressPercent + "%" }}
          transition={{ duration: 0.9, ease: "easeOut" }}
          className="h-full rounded-full bg-brand-gradient"
        />
      </div>

      <div className="mt-6 space-y-3">
        {roadmap.items.map((item, index) => {
          const isInterview = item.skillTag === "Interview";
          return (
            <motion.div
              key={item.id}
              initial={{ opacity: 0, x: -12 }}
              animate={{ opacity: 1, x: 0 }}
              transition={{ duration: 0.35, delay: index * 0.05 }}
              className={
                "flex items-start gap-3 rounded-xl border px-4 py-3.5 transition-colors " +
                (item.completed
                  ? "border-emerald-500/20 bg-emerald-500/5"
                  : "border-white/5 bg-white/[0.02]")
              }
            >
              <button
                type="button"
                onClick={() => !item.completed && onComplete(item.id)}
                disabled={item.completed || completingId === item.id}
                aria-label={item.completed ? "Completed" : "Mark as complete"}
                className={
                  "mt-0.5 flex h-6 w-6 shrink-0 items-center justify-center rounded-full border transition-colors " +
                  (item.completed
                    ? "border-emerald-500 bg-emerald-500 text-white"
                    : "border-white/20 text-transparent hover:border-primary-400")
                }
              >
                {item.completed ? <FiCheck size={13} /> : <FiCircle size={0} />}
              </button>

              <div className="min-w-0 flex-1">
                <p className={"text-sm font-medium " + (item.completed ? "text-slate-400 line-through" : "text-white")}>
                  {item.title}
                </p>
                <div className="mt-1.5 flex items-center gap-2">
                  <span className="inline-flex items-center gap-1 text-xs text-slate-500">
                    {isInterview ? <FiMic size={11} /> : <FiBookOpen size={11} />}
                    {isInterview ? "Interview prep" : "Skill building"}
                  </span>
                  {item.completed && item.completedAt && (
                    <span className="text-xs text-emerald-400">
                      · done {new Date(item.completedAt).toLocaleDateString()}
                    </span>
                  )}
                </div>
              </div>
            </motion.div>
          );
        })}
      </div>
    </div>
  );
}
