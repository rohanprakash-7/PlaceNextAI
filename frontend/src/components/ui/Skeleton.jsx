export function SkeletonBlock({ className = "" }) {
  return <div className={"animate-pulse rounded-2xl bg-slate-200 dark:bg-white/[0.06] " + className} />;
}

export function TableSkeleton({ rows = 6, columns = 6 }) {
  return (
    <div className="space-y-3 p-6">
      {Array.from({ length: rows }).map((_, rowIndex) => (
        <div
          key={rowIndex}
          className="grid gap-4"
          style={{ gridTemplateColumns: `repeat(${columns}, 1fr)` }}
        >
          {Array.from({ length: columns }).map((_, columnIndex) => (
            <div key={columnIndex} className="h-5 animate-pulse rounded-lg bg-slate-200 dark:bg-white/[0.06]" />
          ))}
        </div>
      ))}
    </div>
  );
}
