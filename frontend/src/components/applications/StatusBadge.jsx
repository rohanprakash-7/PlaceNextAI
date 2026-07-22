const STATUS_STYLES = {
  APPLIED: "bg-accent-500/10 text-accent-400",
  SHORTLISTED: "bg-primary-500/10 text-primary-400",
  ASSESSMENT: "bg-amber-500/10 text-amber-400",
  TECHNICAL_INTERVIEW: "bg-amber-500/10 text-amber-400",
  HR_INTERVIEW: "bg-amber-500/10 text-amber-400",
  OFFERED: "bg-emerald-500/10 text-emerald-400",
  HIRED: "bg-emerald-500/10 text-emerald-400",
  REJECTED: "bg-rose-500/10 text-rose-400",
};

export function humanizeStatus(status) {
  return status
    .split("_")
    .map((word) => word.charAt(0) + word.slice(1).toLowerCase())
    .join(" ");
}

export default function StatusBadge({ status }) {
  return (
    <span className={"rounded-full px-2.5 py-1 text-xs font-semibold " + (STATUS_STYLES[status] || STATUS_STYLES.APPLIED)}>
      {humanizeStatus(status)}
    </span>
  );
}
