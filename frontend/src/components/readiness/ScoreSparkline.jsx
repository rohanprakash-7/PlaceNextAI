export default function ScoreSparkline({ history = [], width = 220, height = 48 }) {
  if (history.length < 2) {
    return <p className="text-xs text-slate-500">Score history builds as you use the platform.</p>;
  }

  const values = history.map((point) => point.totalScore);
  const min = Math.min(...values, 0);
  const max = Math.max(...values, 100);
  const range = max - min || 1;

  const points = values
    .map((value, index) => {
      const x = (index / (values.length - 1)) * (width - 4) + 2;
      const y = height - 4 - ((value - min) / range) * (height - 8);
      return `${x},${y}`;
    })
    .join(" ");

  return (
    <svg width={width} height={height} className="overflow-visible" role="img" aria-label="Score history">
      <defs>
        <linearGradient id="sparkline-gradient" x1="0" y1="0" x2="1" y2="0">
          <stop offset="0%" stopColor="#8b5cf6" />
          <stop offset="100%" stopColor="#3b82f6" />
        </linearGradient>
      </defs>
      <polyline
        points={points}
        fill="none"
        stroke="url(#sparkline-gradient)"
        strokeWidth="2"
        strokeLinecap="round"
        strokeLinejoin="round"
      />
    </svg>
  );
}
