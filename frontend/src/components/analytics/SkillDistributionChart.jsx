import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from "recharts";

export default function SkillDistributionChart({ data }) {
  if (!data || data.length === 0) {
    return <p className="py-10 text-center text-sm text-slate-500">No skill data yet.</p>;
  }

  const top = data.slice(0, 8);

  return (
    <ResponsiveContainer width="100%" height={320}>
      <BarChart data={top} layout="vertical" margin={{ left: 24, right: 24 }}>
        <CartesianGrid strokeDasharray="3 3" stroke="rgba(255,255,255,0.06)" horizontal={false} />
        <XAxis type="number" stroke="#64748b" fontSize={12} allowDecimals={false} />
        <YAxis type="category" dataKey="skill" stroke="#64748b" fontSize={12} width={110} />
        <Tooltip
          contentStyle={{
            background: "#131324",
            border: "1px solid rgba(255,255,255,0.1)",
            borderRadius: 12,
            fontSize: 12,
            color: "#e2e8f0",
          }}
        />
        <Legend wrapperStyle={{ fontSize: 12, color: "#94a3b8" }} />
        <Bar dataKey="applicantCount" name="Applicants with skill" fill="#8b5cf6" radius={[0, 6, 6, 0]} />
        <Bar dataKey="requiredCount" name="Jobs requiring skill" fill="#3b82f6" radius={[0, 6, 6, 0]} />
      </BarChart>
    </ResponsiveContainer>
  );
}
