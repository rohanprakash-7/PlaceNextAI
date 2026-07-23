import { useCallback, useEffect, useState } from "react";
import { FiZap } from "react-icons/fi";
import DashboardLayout from "../components/dashboard/DashboardLayout.jsx";
import RankedCandidateCard from "../components/ranking/RankedCandidateCard.jsx";
import CandidateComparisonTable from "../components/ranking/CandidateComparisonTable.jsx";
import EmptyState from "../components/ui/EmptyState.jsx";
import { SkeletonBlock } from "../components/ui/Skeleton.jsx";
import { RECRUITER_NAV } from "../constants";
import { useAuth } from "../context/AuthContext.jsx";
import { getJobsForRecruiter } from "../services/jobService";
import { getRanking, compareCandidates } from "../services/rankingService";

export default function CandidateRankingPage() {
  const { user } = useAuth();

  const [jobs, setJobs] = useState([]);
  const [selectedJobId, setSelectedJobId] = useState("");
  const [candidates, setCandidates] = useState([]);
  const [selectedIds, setSelectedIds] = useState([]);
  const [comparison, setComparison] = useState(null);

  const [loadingJobs, setLoadingJobs] = useState(true);
  const [loadingRanking, setLoadingRanking] = useState(false);
  const [error, setError] = useState("");

  const loadJobs = useCallback(async () => {
    setLoadingJobs(true);
    setError("");
    try {
      const jobList = await getJobsForRecruiter();
      setJobs(jobList);
      if (jobList.length > 0) {
        setSelectedJobId(String(jobList[0].id));
      }
    } catch (err) {
      setError(err.friendlyMessage || "Failed to load your job postings");
    } finally {
      setLoadingJobs(false);
    }
  }, []);

  useEffect(() => {
    loadJobs();
  }, [loadJobs]);

  const loadRanking = useCallback(async (jobId) => {
    if (!jobId) return;
    setLoadingRanking(true);
    setError("");
    setComparison(null);
    setSelectedIds([]);
    try {
      const ranked = await getRanking(jobId);
      setCandidates(ranked);
    } catch (err) {
      setError(err.friendlyMessage || "Failed to rank candidates for this job");
    } finally {
      setLoadingRanking(false);
    }
  }, []);

  useEffect(() => {
    if (selectedJobId) {
      loadRanking(selectedJobId);
    }
  }, [selectedJobId, loadRanking]);

  const toggleSelect = (studentId) => {
    setSelectedIds((current) =>
      current.includes(studentId) ? current.filter((id) => id !== studentId) : [...current, studentId]
    );
  };

  const handleCompare = async () => {
    if (selectedIds.length < 2) return;
    try {
      const result = await compareCandidates(selectedJobId, selectedIds);
      setComparison(result);
    } catch (err) {
      setError(err.friendlyMessage || "Comparison failed");
    }
  };

  return (
    <DashboardLayout
      navItems={RECRUITER_NAV}
      roleLabel="Recruiter"
      title="AI Shortlists"
      userName={user?.name || "User"}
    >
      {loadingJobs && <SkeletonBlock className="h-24" />}

      {!loadingJobs && jobs.length === 0 && (
        <div className="glass-card">
          <EmptyState
            icon={FiZap}
            title="No job postings yet"
            message="Post a job to start seeing an explainable, ranked shortlist of applicants."
          />
        </div>
      )}

      {!loadingJobs && jobs.length > 0 && (
        <>
          <div className="glass-card flex flex-wrap items-center justify-between gap-4 p-5">
            <div className="flex items-center gap-3">
              <FiZap className="text-primary-400" size={16} />
              <div>
                <p className="text-sm font-semibold text-white">Rank candidates for</p>
                <p className="text-xs text-slate-500">Explainable, weighted scoring - not a black box</p>
              </div>
            </div>
            <select
              value={selectedJobId}
              onChange={(event) => setSelectedJobId(event.target.value)}
              className="input-glass w-auto min-w-[220px]"
            >
              {jobs.map((job) => (
                <option key={job.id} value={job.id}>
                  {job.title}
                </option>
              ))}
            </select>
          </div>

          {error && (
            <div className="mt-4 rounded-xl border border-rose-500/30 bg-rose-500/10 px-4 py-3 text-sm text-rose-300" role="alert">
              {error}
            </div>
          )}

          {loadingRanking && (
            <div className="mt-5 space-y-4">
              <SkeletonBlock className="h-40" />
              <SkeletonBlock className="h-40" />
            </div>
          )}

          {!loadingRanking && candidates.length === 0 && !error && (
            <div className="glass-card mt-5">
              <EmptyState title="No applicants yet" message="This job hasn't received any applications yet." />
            </div>
          )}

          {!loadingRanking && candidates.length > 0 && (
            <div className="mt-5 space-y-4">
              <div className="flex items-center justify-between">
                <p className="text-xs text-slate-500">
                  Select two or more candidates to compare them side by side.
                </p>
                <button
                  type="button"
                  onClick={handleCompare}
                  disabled={selectedIds.length < 2}
                  className="glass rounded-xl px-3.5 py-2 text-xs font-medium text-slate-300 transition-colors hover:text-white disabled:cursor-not-allowed disabled:opacity-40"
                >
                  Compare selected ({selectedIds.length})
                </button>
              </div>

              {comparison && <CandidateComparisonTable candidates={comparison} />}

              {candidates.map((candidate, index) => (
                <RankedCandidateCard
                  key={candidate.studentId}
                  candidate={candidate}
                  rank={index}
                  selected={selectedIds.includes(candidate.studentId)}
                  onToggleSelect={toggleSelect}
                />
              ))}
            </div>
          )}
        </>
      )}
    </DashboardLayout>
  );
}
