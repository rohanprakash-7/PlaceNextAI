import { FiSend, FiFileText, FiTrendingUp, FiMic, FiAward, FiLock } from "react-icons/fi";

const ICONS = {
  FiSend,
  FiFileText,
  FiTrendingUp,
  FiMic,
  FiAward,
};

export default function BadgeShelf({ badges = [] }) {
  if (badges.length === 0) {
    return null;
  }

  return (
    <div className="grid grid-cols-2 gap-3 sm:grid-cols-3 lg:grid-cols-5">
      {badges.map((badge) => {
        const Icon = ICONS[badge.icon] || FiAward;
        return (
          <div
            key={badge.code}
            title={badge.description}
            className={
              "flex flex-col items-center gap-2 rounded-xl border px-3 py-4 text-center transition-colors " +
              (badge.earned
                ? "border-primary-500/30 bg-primary-500/10"
                : "border-white/5 bg-white/[0.02] opacity-50")
            }
          >
            <span
              className={
                "flex h-10 w-10 items-center justify-center rounded-xl " +
                (badge.earned ? "bg-brand-gradient text-white" : "bg-white/5 text-slate-500")
              }
            >
              {badge.earned ? <Icon size={18} /> : <FiLock size={16} />}
            </span>
            <span className="text-xs font-medium text-slate-200">{badge.name}</span>
          </div>
        );
      })}
    </div>
  );
}
