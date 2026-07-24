import { useCallback, useEffect, useState } from "react";
import { motion } from "framer-motion";
import { useNavigate } from "react-router-dom";
import { FiMic, FiTarget, FiLoader, FiClock, FiAward } from "react-icons/fi";
import DashboardLayout from "../components/dashboard/DashboardLayout.jsx";
import EmptyState from "../components/ui/EmptyState.jsx";
import ErrorState from "../components/ui/ErrorState.jsx";
import GradientButton from "../components/ui/GradientButton.jsx";
import { SkeletonBlock } from "../components/ui/Skeleton.jsx";
import { STUDENT_NAV } from "../constants";
import { useAuth } from "../context/AuthContext.jsx";
import { getTargetCompanies } from "../services/roadmapService";
import { startInterview, getInterviewHistory } from "../services/interviewService";

export default function MockInterviewPage() {
  const { user } = useAuth();
  const navigate = useNavigate();

  const [companies, setCompanies] = useState([]);
  const [selectedCompany, setSelectedCompany] = useState("");
  const [history, setHistory] = useState([]);
  const [loading, setLoading] = useState(true);
  const [starting, setStarting] = useState(false);
  const [error, setError] = useState("");

  const load = useCallback(async () => {
    setLoading(true);
    setError("");
    try {
      const [companyList, historyList] = await Promise.all([getTargetCompanies(), getInterviewHistory()]);
      setCompanies(companyList);
      setHistory(historyList);
    } catch (err) {
      setError(err.friendlyMessage || "Failed to load interview data");
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    load();
  }, [load]);

  const handleStart = async () => {
    setStarting(true);
    setError("");
    try {
      const session = await startInterview(selectedCompany || null);
      navigate("/dashboard/student/interviews/" + session.id);
    } catch (err) {
      setError(err.friendlyMessage || "Could not start the interview");
      setStarting(false);
    }
  };

  const speechSupported =
    typeof window !== "undefined" && ("SpeechRecognition" in window || "webkitSpeechRecognition" in window);

  return (
    <DashboardLayout navItems={STUDENT_NAV} roleLabel="Student" title="Mock Interviews" userName={user?.name || "Student"}>
      {loading && <SkeletonBlock className="h-64" />}

      {!loading && error && (
        <div className="glass-card">
          <ErrorState message={error} onRetry={load} />
        </div>
      )}

      {!loading && !error && (
        <>
          <motion.div initial={{ opacity: 0, y: 16 }} animate={{ opacity: 1, y: 0 }} className="glass-card p-6">
            <div className="flex items-center gap-2">
              <FiMic className="text-primary-400" size={16} />
              <h2 className="font-display text-lg font-semibold text-slate-900 dark:text-white">
                Start a voice mock interview
              </h2>
            </div>
            <p className="mt-1.5 text-sm text-slate-500">
              The AI interviewer asks each question out loud - answer with your microphone (or type if you'd
              rather). Questions are drawn from your resume skills and, if you pick a company, its open role
              requirements.
            </p>

            {!speechSupported && (
              <p className="mt-3 rounded-lg border border-amber-500/30 bg-amber-500/10 px-3 py-2 text-xs text-amber-500">
                Voice input isn't supported in this browser. Try Chrome or Edge for the full mic experience -
                you can still type your answers here.
              </p>
            )}

            <div className="mt-5">
              <label className="mb-1.5 flex items-center gap-1.5 text-xs font-medium text-slate-500">
                <FiTarget size={13} /> Target company (optional)
              </label>
              <div className="flex flex-wrap gap-2">
                <button
                  type="button"
                  onClick={() => setSelectedCompany("")}
                  className={
                    "rounded-full px-3.5 py-1.5 text-xs font-medium transition-colors " +
                    (selectedCompany === ""
                      ? "bg-brand-gradient text-white"
                      : "glass text-slate-500 hover:text-slate-900 dark:text-slate-400 dark:hover:text-white")
                  }
                >
                  General (my skills)
                </button>
                {companies.map((company) => (
                  <button
                    key={company}
                    type="button"
                    onClick={() => setSelectedCompany(company)}
                    className={
                      "rounded-full px-3.5 py-1.5 text-xs font-medium transition-colors " +
                      (selectedCompany === company
                        ? "bg-brand-gradient text-white"
                        : "glass text-slate-500 hover:text-slate-900 dark:text-slate-400 dark:hover:text-white")
                    }
                  >
                    {company}
                  </button>
                ))}
              </div>
            </div>

            <GradientButton onClick={handleStart} disabled={starting} className="mt-5 w-full">
              {starting ? (
                <>
                  <FiLoader className="animate-spin" size={16} /> Preparing questions…
                </>
              ) : (
                <>
                  <FiMic size={15} /> Start interview
                </>
              )}
            </GradientButton>
          </motion.div>

          <motion.div
            initial={{ opacity: 0, y: 16 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.1 }}
            className="glass-card mt-5 p-6"
          >
            <h2 className="font-display text-lg font-semibold text-slate-900 dark:text-white">Past interviews</h2>
            <div className="mt-4 space-y-2.5">
              {history.length === 0 && (
                <EmptyState
                  icon={FiClock}
                  title="No interviews yet"
                  message="Your completed and in-progress sessions will show up here."
                />
              )}
              {history.map((session) => (
                <button
                  key={session.id}
                  type="button"
                  onClick={() => navigate("/dashboard/student/interviews/" + session.id)}
                  className="flex w-full items-center justify-between rounded-xl border border-slate-200 bg-white/60 px-4 py-3 text-left transition-colors hover:border-primary-500/40 dark:border-white/5 dark:bg-white/[0.03]"
                >
                  <div>
                    <p className="text-sm font-medium text-slate-900 dark:text-white">
                      {session.targetCompany || "General interview"}
                    </p>
                    <p className="mt-0.5 text-xs text-slate-500">
                      {new Date(session.startedAt).toLocaleString()} · {session.status}
                    </p>
                  </div>
                  {session.overallScore != null && (
                    <span className="flex items-center gap-1.5 rounded-full bg-primary-500/10 px-3 py-1 text-xs font-semibold text-primary-500">
                      <FiAward size={12} /> {session.overallScore}/100
                    </span>
                  )}
                </button>
              ))}
            </div>
          </motion.div>
        </>
      )}
    </DashboardLayout>
  );
}
