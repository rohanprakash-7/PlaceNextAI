const DAY_LABELS = ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"];

function bucketColor(count) {
  if (count <= 0) return "bg-white/[0.05]";
  if (count === 1) return "bg-primary-500/25";
  if (count <= 3) return "bg-primary-500/50";
  if (count <= 6) return "bg-primary-500/75";
  return "bg-primary-400";
}

function toKey(date) {
  return date.toISOString().slice(0, 10);
}

export default function WeeklyActivityHeatmap({ counts = [], days = 90 }) {
  const countByDay = new Map(counts.map((entry) => [entry.date, entry.count]));

  const today = new Date();
  today.setHours(0, 0, 0, 0);
  const start = new Date(today);
  start.setDate(start.getDate() - (days - 1));
  // Align the first column to the preceding Sunday so weeks stack cleanly.
  start.setDate(start.getDate() - start.getDay());

  const cells = [];
  const cursor = new Date(start);
  while (cursor <= today) {
    const key = toKey(cursor);
    cells.push({ key, count: countByDay.get(key) || 0 });
    cursor.setDate(cursor.getDate() + 1);
  }

  const weeks = [];
  for (let i = 0; i < cells.length; i += 7) {
    weeks.push(cells.slice(i, i + 7));
  }

  return (
    <div className="overflow-x-auto">
      <div className="flex gap-1">
        <div className="flex flex-col justify-between py-1 pr-1 text-[10px] text-slate-500">
          {DAY_LABELS.filter((_, index) => index % 2 === 1).map((label) => (
            <span key={label}>{label}</span>
          ))}
        </div>
        <div className="flex gap-1">
          {weeks.map((week, weekIndex) => (
            <div key={weekIndex} className="flex flex-col gap-1">
              {week.map((day) => (
                <span
                  key={day.key}
                  title={day.key + " · " + day.count + " event" + (day.count === 1 ? "" : "s")}
                  className={"h-3 w-3 rounded-sm " + bucketColor(day.count)}
                />
              ))}
            </div>
          ))}
        </div>
      </div>
      <div className="mt-3 flex items-center gap-1.5 text-[11px] text-slate-500">
        <span>Less</span>
        <span className="h-3 w-3 rounded-sm bg-white/[0.05]" />
        <span className="h-3 w-3 rounded-sm bg-primary-500/25" />
        <span className="h-3 w-3 rounded-sm bg-primary-500/50" />
        <span className="h-3 w-3 rounded-sm bg-primary-500/75" />
        <span className="h-3 w-3 rounded-sm bg-primary-400" />
        <span>More</span>
      </div>
    </div>
  );
}
