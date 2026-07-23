import { FiCheckCircle } from "react-icons/fi";

export default function RecommendationsList({ recommendations = [] }) {
  if (recommendations.length === 0) {
    return null;
  }

  return (
    <div className="space-y-2.5">
      {recommendations.map((tip) => (
        <div key={tip} className="flex items-start gap-2.5 text-sm text-slate-300">
          <FiCheckCircle className="mt-0.5 shrink-0 text-primary-400" size={15} />
          <span>{tip}</span>
        </div>
      ))}
    </div>
  );
}
