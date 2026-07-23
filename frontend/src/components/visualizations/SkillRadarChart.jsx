import {
  RadarChart,
  PolarGrid,
  PolarAngleAxis,
  PolarRadiusAxis,
  Radar,
  ResponsiveContainer,
} from "recharts";

export default function SkillRadarChart({ readiness }) {
  if (!readiness) {
    return null;
  }

  const data = [
    { dimension: "Academic", value: readiness.academicScore },
    { dimension: "Resume", value: readiness.resumeScore },
    { dimension: "Skills", value: readiness.skillScore },
    { dimension: "Interview", value: readiness.interviewScore },
    { dimension: "Activity", value: readiness.activityScore },
  ];

  return (
    <ResponsiveContainer width="100%" height={280}>
      <RadarChart data={data} outerRadius="75%">
        <PolarGrid stroke="rgba(255,255,255,0.1)" />
        <PolarAngleAxis dataKey="dimension" tick={{ fill: "#94a3b8", fontSize: 12 }} />
        <PolarRadiusAxis angle={90} domain={[0, 100]} tick={{ fill: "#64748b", fontSize: 10 }} />
        <Radar dataKey="value" stroke="#8b5cf6" fill="#8b5cf6" fillOpacity={0.35} />
      </RadarChart>
    </ResponsiveContainer>
  );
}
