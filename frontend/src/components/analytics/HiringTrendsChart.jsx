import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from "recharts";

export default function HiringTrendsChart({ trends = [] }) {
  if (trends.every((entry) => entry.applications === 0 && entry.hires === 0)) {
    return <p className="py-10 text-center text-sm text-slate-500">No application activity in the last 6 months.</p>;
  }

  return (
    <ResponsiveContainer width="100%" height={260}>
      <LineChart data={trends} margin={{ top: 10, right: 20, left: -10, bottom: 0 }}>
        <CartesianGrid strokeDasharray="3 3" stroke="rgba(255,255,255,0.06)" />
        <XAxis dataKey="month" stroke="#64748b" fontSize={12} />
        <YAxis stroke="#64748b" fontSize={12} allowDecimals={false} />
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
        <Line type="monotone" dataKey="applications" name="Applications" stroke="#8b5cf6" strokeWidth={2} dot={{ r: 3 }} />
        <Line type="monotone" dataKey="hires" name="Hires" stroke="#34d399" strokeWidth={2} dot={{ r: 3 }} />
      </LineChart>
    </ResponsiveContainer>
  );
}
