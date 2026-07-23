import { PieChart, Pie, Cell, Legend, Tooltip, ResponsiveContainer } from "recharts";

const COLORS = { Low: "#34d399", Medium: "#fbbf24", High: "#fb7185" };

export default function RiskDistributionChart({ distribution }) {
  const data = [
    { name: "Low", value: distribution?.low || 0 },
    { name: "Medium", value: distribution?.medium || 0 },
    { name: "High", value: distribution?.high || 0 },
  ];

  if (data.every((entry) => entry.value === 0)) {
    return <p className="py-10 text-center text-sm text-slate-500">No prediction data yet.</p>;
  }

  return (
    <ResponsiveContainer width="100%" height={260}>
      <PieChart>
        <Pie data={data} dataKey="value" nameKey="name" innerRadius={60} outerRadius={90} paddingAngle={3}>
          {data.map((entry) => (
            <Cell key={entry.name} fill={COLORS[entry.name]} stroke="none" />
          ))}
        </Pie>
        <Legend wrapperStyle={{ fontSize: 12, color: "#94a3b8" }} />
        <Tooltip
          contentStyle={{
            background: "#131324",
            border: "1px solid rgba(255,255,255,0.1)",
            borderRadius: 12,
            fontSize: 12,
            color: "#e2e8f0",
          }}
        />
      </PieChart>
    </ResponsiveContainer>
  );
}
