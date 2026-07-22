import { useCallback, useEffect, useState } from "react";
import { motion, AnimatePresence } from "framer-motion";
import { FiChevronDown, FiSend } from "react-icons/fi";
import DashboardLayout from "../components/dashboard/DashboardLayout.jsx";
import StatusBadge from "../components/applications/StatusBadge.jsx";
import TimelineStages from "../components/applications/TimelineStages.jsx";
import FeedbackSummaryCard from "../components/applications/FeedbackSummaryCard.jsx";
import EmptyState from "../components/ui/EmptyState.jsx";
import ErrorState from "../components/ui/ErrorState.jsx";
import { SkeletonBlock } from "../components/ui/Skeleton.jsx";
import { STUDENT_NAV } from "../constants";
import { useAuth } from "../context/AuthContext.jsx";
import {
  getStudentApplications,
  getApplicationTimeline,
  getFeedbackSummary,
} from "../services/applicationService";

function ApplicationRow({ application }) {
  const [expanded, setExpanded] = useState(false);
  const [timeline, setTimeline] = useState(null);
  const [loadingTimeline, setLoadingTimeline] = useState(false);

  const toggle = async () => {
    if (!expanded && !timeline) {
      setLoadingTimeline(true);
      try {
        const data = await getApplicationTimeline(application.id);
        setTimeline(data);
      } finally {
        setLoadingTimeline(false);
      }
    }
    setExpanded((current) => !current);
  };

  return (
    <div className="rounded-xl border border-white/5 bg-white/[0.02]">
      <button
        type="button"
        onClick={toggle}
        className="flex w-full items-center justify-between gap-3 px-4 py-3.5 text-left"
      >
        <div>
          <p className="text-sm font-medium text-white">{application.jobTitle}</p>
          <p className="mt-0.5 text-xs text-slate-500">{application.company}</p>
        </div>
        <div className="flex items-center gap-3">
          <StatusBadge status={application.status} />
          <FiChevronDown
            size={15}
            className={"text-slate-500 transition-transform " + (expanded ? "rotate-180" : "")}
          />
        </div>
      </button>

      <AnimatePresence>
        {expanded && (
          <motion.div
            initial={{ opacity: 0, height: 0 }}
            animate={{ opacity: 1, height: "auto" }}
            exit={{ opacity: 0, height: 0 }}
            className="overflow-hidden border-t border-white/5"
          >
            <div className="p-4">
              {loadingTimeline && <SkeletonBlock className="h-32" />}
              {!loadingTimeline && timeline && <TimelineStages timeline={timeline} />}
            </div>
          </motion.div>
        )}
      </AnimatePresence>
    </div>
  );
}

export default function StudentApplicationsPage() {
  const { user } = useAuth();

  const [applications, setApplications] = useState([]);
  const [summary, setSummary] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const load = useCallback(async () => {
    setLoading(true);
    setError("");
    try {
      const [applicationsData, summaryData] = await Promise.all([
        getStudentApplications(),
        getFeedbackSummary(),
      ]);
      setApplications(applicationsData);
      setSummary(summaryData);
    } catch (err) {
      setError(err.friendlyMessage || "Failed to load your applications");
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    load();
  }, [load]);

  return (
    <DashboardLayout
      navItems={STUDENT_NAV}
      roleLabel="Student"
      title="My Applications"
      userName={user?.name || "Student"}
    >
      {loading && (
        <div className="grid gap-5 lg:grid-cols-3">
          <SkeletonBlock className="h-64 lg:col-span-2" />
          <SkeletonBlock className="h-64" />
        </div>
      )}

      {!loading && error && (
        <div className="glass-card">
          <ErrorState message={error} onRetry={load} />
        </div>
      )}

      {!loading && !error && (
        <div className="grid gap-5 lg:grid-cols-3">
          <div className="glass-card p-6 lg:col-span-2">
            <h2 className="font-display text-lg font-semibold text-white">Application timeline</h2>
            <p className="mt-1 text-xs text-slate-500">Click any application to see its interview stages.</p>

            {applications.length === 0 ? (
              <EmptyState
                icon={FiSend}
                title="No applications yet"
                message="Browse open roles and apply to start tracking your progress here."
              />
            ) : (
              <div className="mt-5 space-y-3">
                {applications.map((application) => (
                  <ApplicationRow key={application.id} application={application} />
                ))}
              </div>
            )}
          </div>

          {summary && <FeedbackSummaryCard summary={summary} />}
        </div>
      )}
    </DashboardLayout>
  );
}
