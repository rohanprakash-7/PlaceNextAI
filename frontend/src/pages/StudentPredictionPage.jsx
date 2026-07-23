import { useCallback, useEffect, useState } from "react";
import { FiRefreshCw, FiPieChart } from "react-icons/fi";
import DashboardLayout from "../components/dashboard/DashboardLayout.jsx";
import ProbabilityGauge from "../components/prediction/ProbabilityGauge.jsx";
import FactorsList from "../components/prediction/FactorsList.jsx";
import RecommendationsList from "../components/prediction/RecommendationsList.jsx";
import { SkeletonBlock } from "../components/ui/Skeleton.jsx";
import ErrorState from "../components/ui/ErrorState.jsx";
import { STUDENT_NAV } from "../constants";
import { useAuth } from "../context/AuthContext.jsx";
import { getPrediction, recomputePrediction } from "../services/predictionService";

export default function StudentPredictionPage() {
  const { user } = useAuth();

  const [prediction, setPrediction] = useState(null);
  const [loading, setLoading] = useState(true);
  const [recomputing, setRecomputing] = useState(false);
  const [error, setError] = useState("");

  const load = useCallback(async () => {
    setLoading(true);
    setError("");
    try {
      const data = await getPrediction();
      setPrediction(data);
    } catch (err) {
      setError(err.friendlyMessage || "Failed to load your placement prediction");
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    load();
  }, [load]);

  const handleRecompute = async () => {
    setRecomputing(true);
    try {
      const updated = await recomputePrediction();
      setPrediction(updated);
    } catch (err) {
      setError(err.friendlyMessage || "Recompute failed");
    } finally {
      setRecomputing(false);
    }
  };

  return (
    <DashboardLayout
      navItems={STUDENT_NAV}
      roleLabel="Student"
      title="Placement Prediction"
      userName={user?.name || "Student"}
    >
      {loading && <SkeletonBlock className="h-80" />}

      {!loading && error && !prediction && (
        <div className="glass-card">
          <ErrorState message={error} onRetry={load} />
        </div>
      )}

      {!loading && prediction && (
        <div className="glass-card p-6">
          <div className="flex flex-wrap items-start justify-between gap-4">
            <div>
              <div className="flex items-center gap-2">
                <FiPieChart className="text-primary-400" size={16} />
                <h2 className="font-display text-lg font-semibold text-white">Placement Probability</h2>
              </div>
              <p className="mt-1 text-xs text-slate-500">
                Explainable, not a black box · last updated{" "}
                {new Date(prediction.computedAt).toLocaleString()}
              </p>
            </div>
            <button
              type="button"
              onClick={handleRecompute}
              disabled={recomputing}
              className="glass inline-flex items-center gap-2 rounded-xl px-3.5 py-2 text-xs font-medium text-slate-300 transition-colors hover:text-white disabled:opacity-50"
            >
              <FiRefreshCw size={13} className={recomputing ? "animate-spin" : ""} />
              {recomputing ? "Recomputing…" : "Refresh"}
            </button>
          </div>

          <div className="mt-6 flex flex-col gap-8 lg:flex-row lg:items-start">
            <ProbabilityGauge probabilityScore={prediction.probabilityScore} riskLevel={prediction.riskLevel} />

            <div className="flex-1 space-y-6">
              <div>
                <h3 className="mb-3 text-sm font-semibold text-white">What's driving this score</h3>
                <FactorsList
                  positiveFactors={prediction.positiveFactors}
                  negativeFactors={prediction.negativeFactors}
                />
              </div>

              {prediction.recommendations?.length > 0 && (
                <div>
                  <h3 className="mb-3 text-sm font-semibold text-white">Recommendations</h3>
                  <RecommendationsList recommendations={prediction.recommendations} />
                </div>
              )}
            </div>
          </div>
        </div>
      )}
    </DashboardLayout>
  );
}
