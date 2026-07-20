import { useCallback, useEffect, useState } from "react";
import { FiFileText } from "react-icons/fi";
import DashboardLayout from "../components/dashboard/DashboardLayout.jsx";
import ResumeUploadCard from "../components/resume/ResumeUploadCard.jsx";
import AnalysisPanel from "../components/resume/AnalysisPanel.jsx";
import VersionTimeline from "../components/resume/VersionTimeline.jsx";
import EmptyState from "../components/ui/EmptyState.jsx";
import ErrorState from "../components/ui/ErrorState.jsx";
import { SkeletonBlock } from "../components/ui/Skeleton.jsx";
import { STUDENT_NAV } from "../constants";
import { useAuth } from "../context/AuthContext.jsx";
import { uploadResume, getResumeVersions } from "../services/resumeService";

export default function StudentResumePage() {
  const { user } = useAuth();

  const [versions, setVersions] = useState([]);
  const [selected, setSelected] = useState(null);
  const [loading, setLoading] = useState(true);
  const [uploading, setUploading] = useState(false);
  const [error, setError] = useState("");
  const [uploadError, setUploadError] = useState("");

  const load = useCallback(async () => {
    setLoading(true);
    setError("");
    try {
      const data = await getResumeVersions();
      setVersions(data);
      setSelected((current) => current || data[0] || null);
    } catch (err) {
      setError(err.friendlyMessage || "Failed to load resume versions");
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    load();
  }, [load]);

  const handleUpload = async (file, jobDescription) => {
    setUploading(true);
    setUploadError("");
    try {
      const created = await uploadResume(file, jobDescription);
      setSelected(created);
      await load();
    } catch (err) {
      setUploadError(err.friendlyMessage || "Upload failed. Is the AI service running on port 8000?");
    } finally {
      setUploading(false);
    }
  };

  return (
    <DashboardLayout
      navItems={STUDENT_NAV}
      roleLabel="Student"
      title="Resume Analyzer"
      userName={user?.name || "Student"}
    >
      <div className="grid gap-5 lg:grid-cols-3">
        <div className="space-y-5">
          <ResumeUploadCard onUpload={handleUpload} uploading={uploading} />
          {uploadError && (
            <div className="rounded-xl border border-rose-500/30 bg-rose-500/10 px-4 py-3 text-sm text-rose-300" role="alert">
              {uploadError}
            </div>
          )}
          {!loading && !error && versions.length > 0 && (
            <VersionTimeline
              versions={versions}
              selectedId={selected?.id}
              onSelect={setSelected}
            />
          )}
        </div>

        <div className="lg:col-span-2">
          {loading && <SkeletonBlock className="h-96" />}
          {!loading && error && (
            <div className="glass-card">
              <ErrorState message={error} onRetry={load} />
            </div>
          )}
          {!loading && !error && !selected && (
            <div className="glass-card">
              <EmptyState
                icon={FiFileText}
                title="No resume analyzed yet"
                message="Upload your first PDF on the left. Each upload becomes a version, so you can track your ATS score improving over time — and every analysis raises your Readiness Score."
              />
            </div>
          )}
          {!loading && !error && selected && <AnalysisPanel version={selected} />}
        </div>
      </div>
    </DashboardLayout>
  );
}
