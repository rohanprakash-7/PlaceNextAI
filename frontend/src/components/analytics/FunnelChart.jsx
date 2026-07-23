import { FunnelChart as RechartsFunnelChart, Funnel, LabelList, Tooltip, ResponsiveContainer } from "recharts";

const PALETTE = ["#8b5cf6", "#7c6cf0", "#6d78eb", "#5e84e6", "#4f90e1", "#3b82f6", "#2563eb"];

export default function FunnelChart({ data }) {
  if (!data || data.length === 0) {
    return <p className="py-10 text-center text-sm text-slate-500">No funnel data yet.</p>;
  }

  const shaped = data.map((entry, index) => ({
    ...entry,
    fill: PALETTE[index % PALETTE.length],
  }));

  return (
    <ResponsiveContainer width="100%" height={320}>
      <RechartsFunnelChart>
        <Tooltip
          contentStyle={{
            background: "#131324",
            border: "1px solid rgba(255,255,255,0.1)",
            borderRadius: 12,
            fontSize: 12,
            color: "#e2e8f0",
          }}
        />
        <Funnel dataKey="value" data={shaped} isAnimationActive>
          <LabelList position="right" dataKey="name" stroke="none" fill="#e2e8f0" fontSize={12} />
          <LabelList position="center" dataKey="value" stroke="none" fill="#ffffff" fontSize={13} fontWeight={700} />
        </Funnel>
      </RechartsFunnelChart>
    </ResponsiveContainer>
  );
}
