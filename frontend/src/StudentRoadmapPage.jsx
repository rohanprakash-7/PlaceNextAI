import { useCallback, useEffect, useState } from "react";
import { FiTrendingUp } from "react-icons/fi";
import DashboardLayout from "../components/dashboard/DashboardLayout.jsx";
import CompanyPicker from "../components/roadmap/CompanyPicker.jsx";
import SkillGapPanel from "../components/roadmap/SkillGapPanel.jsx";
import RoadmapTimeline from "../components/roadmap/RoadmapTimeline.jsx";
import EmptyState from "../components/ui/EmptyState.jsx";
import ErrorState from "../components/ui/ErrorState.jsx";
import { SkeletonBlock } from "../components/ui/Skeleton.jsx";
import { STUDENT_NAV } from "../constants";
import { useAuth } from "../context/AuthContext.jsx";
import {
  getSkillGap,
  getTargetCompanies,
  generateRoadmap,
  getActiveRoadmap,
  completeRoadmapItem,
} from "../services/roadmapService";

export default function StudentRoadmapPage() {
  const { user } = useAuth();

  const [companies, setCompanies] = useState([]);
  const [selectedCompany, setSelectedCompany] = useState("");
  const [gap, setGap] = useState(null);
  const [roadmap, setRoadmap] = useState(null);

  const [loading, setLoading] = useState(true);
  const [analyzing, setAnalyzing] = useState(false);
  const [generating, setGenerating] = useState(false);
  const [completingId, setCompletingId] = useState(null);
  const [error, setError] = useState("");

  const bootstrap = useCallback(async () => {
    setLoading(true);
    setError("");
    try {
      const companyList = await getTargetCompanies();
      setCompanies(companyList);
      try {
        const existing = await getActiveRoadmap();
        setRoadmap(existing);
      } catch {
        // No roadmap yet - that's a normal first-time state, not an error.
      }
    } catch (err) {
      setError(err.friendlyMessage || "Failed to load skill gap data");
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    bootstrap();
  }, [bootstrap]);

  const handleAnalyze = async () => {
    setAnalyzing(true);
    setError("");
    try {
      const result = await getSkillGap(selectedCompany);
      setGap(result);
    } catch (err) {
      setError(err.friendlyMessage || "Analysis failed");
    } finally {
      setAnalyzing(false);
    }
  };

  const handleGenerate = async () => {
    setGenerating(true);
    try {
      const created = await generateRoadmap(selectedCompany);
      setRoadmap(created);
    } catch (err) {
      setError(err.friendlyMessage || "Could not generate roadmap");
    } finally {
      setGenerating(false);
    }
  };

  const handleComplete = async (itemId) => {
    setCompletingId(itemId);
    try {
      const updated = await completeRoadmapItem(itemId);
      setRoadmap(updated);
    } catch (err) {
      setError(err.friendlyMessage || "Could not update item");
    } finally {
      setCompletingId(null);
    }
  };

  return (
    <DashboardLayout
      navItems={STUDENT_NAV}
      roleLabel="Student"
      title="Skill Roadmap"
      userName={user?.name || "Student"}
    >
      {loading && <SkeletonBlock className="h-64" />}

      {!loading && error && !gap && !roadmap && (
        <div className="glass-card">
          <ErrorState message={error} onRetry={bootstrap} />
        </div>
      )}

      {!loading && (
        <div className="grid gap-5 lg:grid-cols-3">
          <div className="space-y-5 lg:col-span-1">
            <CompanyPicker
              companies={companies}
              selected={selectedCompany}
              onSelect={setSelectedCompany}
              onAnalyze={handleAnalyze}
              loading={analyzing}
            />
          </div>

          <div className="space-y-5 lg:col-span-2">
            {error && (
              <div className="rounded-xl border border-rose-500/30 bg-rose-500/10 px-4 py-3 text-sm text-rose-300" role="alert">
                {error}
              </div>
            )}

            {gap && <SkillGapPanel gap={gap} onGenerate={handleGenerate} generating={generating} />}

            {roadmap ? (
              <RoadmapTimeline roadmap={roadmap} onComplete={handleComplete} completingId={completingId} />
            ) : (
              !gap && (
                <div className="glass-card">
                  <EmptyState
                    icon={FiTrendingUp}
                    title="No roadmap yet"
                    message="Pick a target company on the left and analyze your skill gap to generate a personalized week-by-week learning plan."
                  />
                </div>
              )
            )}
          </div>
        </div>
      )}
    </DashboardLayout>
  );
}
