import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from "recharts";

export default function SkillsBarChart({ data = [], color = "#8b5cf6" }) {
  if (data.length === 0) {
    return <p className="py-10 text-center text-sm text-slate-500">No skill data yet.</p>;
  }

  return (
    <ResponsiveContainer width="100%" height={Math.max(220, data.length * 34)}>
      <BarChart data={data} layout="vertical" margin={{ top: 5, right: 20, left: 10, bottom: 5 }}>
        <CartesianGrid strokeDasharray="3 3" stroke="rgba(255,255,255,0.06)" horizontal={false} />
        <XAxis type="number" stroke="#64748b" fontSize={12} allowDecimals={false} />
        <YAxis type="category" dataKey="skill" stroke="#94a3b8" fontSize={12} width={110} />
        <Tooltip
          cursor={{ fill: "rgba(255,255,255,0.04)" }}
          contentStyle={{
            background: "#131324",
            border: "1px solid rgba(255,255,255,0.1)",
            borderRadius: 12,
            fontSize: 12,
            color: "#e2e8f0",
          }}
        />
        <Bar dataKey="count" fill={color} radius={[0, 6, 6, 0]} />
      </BarChart>
    </ResponsiveContainer>
  );
}
