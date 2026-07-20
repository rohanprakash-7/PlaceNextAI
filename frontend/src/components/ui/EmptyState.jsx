import { FiInbox } from "react-icons/fi";

export default function EmptyState({
  icon: Icon = FiInbox,
  title = "Nothing here yet",
  message = "This section will be populated in an upcoming phase.",
  action,
}) {
  return (
    <div className="flex flex-col items-center justify-center px-6 py-16 text-center">
      <span className="glass flex h-14 w-14 items-center justify-center rounded-2xl text-slate-400">
        <Icon size={22} />
      </span>
      <h3 className="mt-4 font-display text-base font-semibold text-white">{title}</h3>
      <p className="mt-1.5 max-w-sm text-sm leading-relaxed text-slate-500">{message}</p>
      {action && <div className="mt-5">{action}</div>}
    </div>
  );
}
