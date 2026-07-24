import { useCallback, useEffect, useState } from "react";
import { motion } from "framer-motion";
import { FiMessageSquare, FiMail, FiCheck, FiX, FiLoader } from "react-icons/fi";
import { Link } from "react-router-dom";
import DashboardLayout from "../components/dashboard/DashboardLayout.jsx";
import EmptyState from "../components/ui/EmptyState.jsx";
import ErrorState from "../components/ui/ErrorState.jsx";
import { SkeletonBlock } from "../components/ui/Skeleton.jsx";
import { ALUMNI_NAV } from "../constants";
import { useAuth } from "../context/AuthContext.jsx";
import { acceptMentorRequest, getIncomingMentorRequests, rejectMentorRequest } from "../services/mentorService";

const STATUS_STYLES = {
  PENDING: "bg-amber-500/10 text-amber-400",
  ACCEPTED: "bg-emerald-500/10 text-emerald-400",
  REJECTED: "bg-rose-500/10 text-rose-400",
};

export default function AlumniMentorRequestsPage() {
  const { user } = useAuth();
  const [requests, setRequests] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [actingId, setActingId] = useState(null);

  const load = useCallback(async () => {
    setLoading(true);
    setError("");
    try {
      setRequests(await getIncomingMentorRequests());
    } catch (err) {
      setError(err.friendlyMessage || "Failed to load mentor requests");
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    load();
  }, [load]);

  const respond = async (id, action) => {
    setActingId(id);
    setError("");
    try {
      const updated = action === "accept" ? await acceptMentorRequest(id) : await rejectMentorRequest(id);
      setRequests((prev) => prev.map((request) => (request.id === id ? updated : request)));
    } catch (err) {
      setError(err.friendlyMessage || "Could not update this request");
    } finally {
      setActingId(null);
    }
  };

  return (
    <DashboardLayout navItems={ALUMNI_NAV} roleLabel="Alumni" title="Mentor Requests" userName={user?.name || "User"}>
      {loading && <SkeletonBlock className="h-96" />}

      {!loading && error && requests.length === 0 && (
        <div className="glass-card">
          <ErrorState message={error} onRetry={load} />
        </div>
      )}

      {!loading && error && requests.length > 0 && (
        <p className="mb-4 rounded-lg border border-rose-500/30 bg-rose-500/10 px-3.5 py-2.5 text-xs text-rose-300">
          {error}
        </p>
      )}

      {!loading && !error && requests.length === 0 && (
        <div className="glass-card">
          <EmptyState
            icon={FiMessageSquare}
            title="No mentorship requests yet"
            message="When a student asks you for career guidance, resume feedback or interview prep, it will show up here."
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
                  <p className="font-display text-base font-semibold text-slate-900 dark:text-white">{request.studentName}</p>
                  <span className={"rounded-full px-2.5 py-1 text-[11px] font-semibold " + STATUS_STYLES[request.status]}>
                    {request.status}
                  </span>
                </div>
                <p className="mt-1 text-xs text-slate-500">{request.topic}</p>
                <p className="mt-2 max-w-xl text-sm text-slate-500 dark:text-slate-400">{request.message}</p>
              </div>

              <div className="flex items-center gap-2">
                {request.status === "PENDING" && (
                  <>
                    <button
                      type="button"
                      onClick={() => respond(request.id, "accept")}
                      disabled={actingId === request.id}
                      className="inline-flex items-center gap-1.5 rounded-xl bg-brand-gradient px-3.5 py-2 text-xs font-semibold text-slate-900 dark:text-white shadow-glow-sm disabled:opacity-50"
                    >
                      {actingId === request.id ? <FiLoader className="animate-spin" size={13} /> : <FiCheck size={13} />} Accept
                    </button>
                    <button
                      type="button"
                      onClick={() => respond(request.id, "reject")}
                      disabled={actingId === request.id}
                      className="glass inline-flex items-center gap-1.5 rounded-xl px-3.5 py-2 text-xs font-medium text-slate-700 dark:text-slate-300 transition-colors hover:text-slate-900 dark:hover:text-white disabled:opacity-50"
                    >
                      <FiX size={13} /> Decline
                    </button>
                  </>
                )}
                {request.status === "ACCEPTED" && (
                  <Link
                    to={"/dashboard/alumni/requests/" + request.id + "/messages"}
                    className="glass inline-flex items-center gap-2 rounded-xl px-3.5 py-2 text-xs font-medium text-slate-700 dark:text-slate-300 transition-colors hover:text-slate-900 dark:hover:text-white"
                  >
                    <FiMail size={13} /> Open chat
                  </Link>
                )}
              </div>
            </motion.div>
          ))}
        </div>
      )}
    </DashboardLayout>
  );
}
