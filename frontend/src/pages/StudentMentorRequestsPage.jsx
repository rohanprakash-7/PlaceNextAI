import { useCallback, useEffect, useState } from "react";
import { motion } from "framer-motion";
import { FiMessageSquare, FiMail } from "react-icons/fi";
import { Link } from "react-router-dom";
import DashboardLayout from "../components/dashboard/DashboardLayout.jsx";
import EmptyState from "../components/ui/EmptyState.jsx";
import ErrorState from "../components/ui/ErrorState.jsx";
import { SkeletonBlock } from "../components/ui/Skeleton.jsx";
import { STUDENT_NAV } from "../constants";
import { useAuth } from "../context/AuthContext.jsx";
import { getMyMentorRequests } from "../services/mentorService";

const STATUS_STYLES = {
  PENDING: "bg-amber-500/10 text-amber-400",
  ACCEPTED: "bg-emerald-500/10 text-emerald-400",
  REJECTED: "bg-rose-500/10 text-rose-400",
};

export default function StudentMentorRequestsPage() {
  const { user } = useAuth();
  const [requests, setRequests] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const load = useCallback(async () => {
    setLoading(true);
    setError("");
    try {
      setRequests(await getMyMentorRequests());
    } catch (err) {
      setError(err.friendlyMessage || "Failed to load your mentor requests");
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    load();
  }, [load]);

  return (
    <DashboardLayout navItems={STUDENT_NAV} roleLabel="Student" title="Mentor Requests" userName={user?.name || "Student"}>
      {loading && <SkeletonBlock className="h-96" />}

      {!loading && error && requests.length === 0 && (
        <div className="glass-card">
          <ErrorState message={error} onRetry={load} />
        </div>
      )}

      {!loading && !error && requests.length === 0 && (
        <div className="glass-card">
          <EmptyState
            icon={FiMessageSquare}
            title="No mentor requests yet"
            message="Visit the Mentors page and ask an alumnus for career guidance, resume feedback or interview prep."
          />
        </div>
      )}

      {!loading && requests.length > 0 && (
        <div className="space-y-3">
          {requests.map((request, index) => (
            <motion.div
              key={request.id}
              initial={{ opacity: 0, y: 12 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: index * 0.05 }}
              className="glass-card flex flex-wrap items-center justify-between gap-4 p-5"
            >
              <div>
                <div className="flex items-center gap-2">
                  <p className="font-display text-base font-semibold text-slate-900 dark:text-white">{request.alumniName}</p>
                  <span className={"rounded-full px-2.5 py-1 text-[11px] font-semibold " + STATUS_STYLES[request.status]}>
                    {request.status}
                  </span>
                </div>
                <p className="mt-1 text-xs text-slate-500">
                  {request.topic} {request.alumniCompany ? "· " + request.alumniCompany : ""}
                </p>
                <p className="mt-2 max-w-xl text-sm text-slate-500 dark:text-slate-400">{request.message}</p>
              </div>

              {request.status === "ACCEPTED" && (
                <Link
                  to={"/dashboard/student/mentors/requests/" + request.id + "/messages"}
                  className="glass inline-flex items-center gap-2 rounded-xl px-3.5 py-2 text-xs font-medium text-slate-700 dark:text-slate-300 transition-colors hover:text-slate-900 dark:hover:text-white"
                >
                  <FiMail size={13} /> Open chat
                </Link>
              )}
            </motion.div>
          ))}
        </div>
      )}
    </DashboardLayout>
  );
}
