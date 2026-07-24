import { useCallback, useEffect, useState } from "react";
import { motion } from "framer-motion";
import { FiCalendar, FiUsers, FiClock, FiSend, FiLoader, FiMessageSquare, FiStar } from "react-icons/fi";
import { Link } from "react-router-dom";
import DashboardLayout from "../components/dashboard/DashboardLayout.jsx";
import StatCard from "../components/ui/StatCard.jsx";
import ErrorState from "../components/ui/ErrorState.jsx";
import EmptyState from "../components/ui/EmptyState.jsx";
import GradientButton from "../components/ui/GradientButton.jsx";
import { SkeletonBlock } from "../components/ui/Skeleton.jsx";
import { ALUMNI_NAV } from "../constants";
import { useAuth } from "../context/AuthContext.jsx";
import {
  getMySlots,
  getMyAlumniSessions,
  getIncomingMentorRequests,
  getMyAlumniProfile,
  postInterviewExperience,
} from "../services/mentorService";

export default function AlumniDashboard() {
  const { user } = useAuth();

  const [slots, setSlots] = useState([]);
  const [sessions, setSessions] = useState([]);
  const [pendingRequests, setPendingRequests] = useState(0);
  const [rating, setRating] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [form, setForm] = useState({ company: "", roleTitle: "", content: "" });
  const [posting, setPosting] = useState(false);
  const [posted, setPosted] = useState(false);

  const load = useCallback(async () => {
    setLoading(true);
    setError("");
    try {
      const [slotData, sessionData, requestData, profileData] = await Promise.all([
        getMySlots(),
        getMyAlumniSessions(),
        getIncomingMentorRequests(),
        getMyAlumniProfile(),
      ]);
      setSlots(slotData);
      setSessions(sessionData);
      setPendingRequests(requestData.filter((request) => request.status === "PENDING").length);
      setRating(profileData.averageRating);
    } catch (err) {
      setError(err.friendlyMessage || "Failed to load your mentor activity");
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    load();
  }, [load]);

  const handlePost = async (event) => {
    event.preventDefault();
    setPosting(true);
    setPosted(false);
    try {
      await postInterviewExperience(form);
      setForm({ company: "", roleTitle: "", content: "" });
      setPosted(true);
    } catch (err) {
      setError(err.friendlyMessage || "Could not post your experience");
    } finally {
      setPosting(false);
    }
  };

  const openSlots = slots.filter((slot) => !slot.booked).length;

  return (
    <DashboardLayout navItems={ALUMNI_NAV} roleLabel="Alumni" title="Alumni Overview" userName={user?.name || "User"}>
      {loading && (
        <div className="grid gap-5 lg:grid-cols-2">
          <SkeletonBlock className="h-32 lg:col-span-2" />
          <SkeletonBlock className="h-64" />
          <SkeletonBlock className="h-64" />
        </div>
      )}

      {!loading && error && (
        <div className="glass-card">
          <ErrorState message={error} onRetry={load} />
        </div>
      )}

      {!loading && !error && (
        <>
          <div className="grid gap-4 sm:grid-cols-5">
            <StatCard icon={FiCalendar} label="Slots created" value={slots.length} />
            <StatCard icon={FiClock} label="Open slots" value={openSlots} />
            <StatCard icon={FiUsers} label="Booked sessions" value={sessions.length} />
            <Link to="/dashboard/alumni/requests">
              <StatCard icon={FiMessageSquare} label="Pending requests" value={pendingRequests} />
            </Link>
            <StatCard icon={FiStar} label="Average rating" value={rating ? rating.toFixed(1) : "—"} />
          </div>

          <div className="mt-6 grid gap-5 lg:grid-cols-2">
            <motion.div initial={{ opacity: 0, y: 16 }} animate={{ opacity: 1, y: 0 }} className="glass-card p-6">
              <h2 className="font-display text-lg font-semibold text-slate-900 dark:text-white">Upcoming sessions</h2>
              <div className="mt-4 space-y-3">
                {sessions.length === 0 && (
                  <EmptyState
                    icon={FiUsers}
                    title="No sessions booked yet"
                    message="Once a student books one of your open slots, it will show up here."
                  />
                )}
                {sessions.map((session) => (
                  <div
                    key={session.id}
                    className="rounded-xl border border-slate-200 dark:border-white/5 bg-slate-50 dark:bg-white/[0.03] px-4 py-3.5"
                  >
                    <p className="text-sm font-medium text-slate-900 dark:text-white">
                      {new Date(session.startTime).toLocaleString()}
                    </p>
                    <p className="mt-0.5 text-xs text-slate-500">
                      Ends {new Date(session.endTime).toLocaleTimeString()}
                    </p>
                  </div>
                ))}
              </div>
            </motion.div>

            <motion.div
              initial={{ opacity: 0, y: 16 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: 0.1 }}
              className="glass-card p-6"
            >
              <h2 className="font-display text-lg font-semibold text-slate-900 dark:text-white">Share an interview experience</h2>
              <p className="mt-1 text-xs text-slate-500">
                Visible to students on the job listing for that company.
              </p>
              {posted && (
                <p className="mt-3 rounded-lg border border-emerald-500/30 bg-emerald-500/10 px-3 py-2 text-xs text-emerald-300">
                  Posted successfully.
                </p>
              )}
              <form onSubmit={handlePost} className="mt-4 space-y-3">
                <input
                  type="text"
                  required
                  placeholder="Company"
                  value={form.company}
                  onChange={(event) => setForm({ ...form, company: event.target.value })}
                  className="input-glass"
                />
                <input
                  type="text"
                  required
                  placeholder="Role title"
                  value={form.roleTitle}
                  onChange={(event) => setForm({ ...form, roleTitle: event.target.value })}
                  className="input-glass"
                />
                <textarea
                  required
                  rows={4}
                  placeholder="Share what the interview process was like..."
                  value={form.content}
                  onChange={(event) => setForm({ ...form, content: event.target.value })}
                  className="input-glass resize-none"
                />
                <GradientButton type="submit" disabled={posting} className="w-full">
                  {posting ? (
                    <>
                      <FiLoader className="animate-spin" size={16} /> Posting…
                    </>
                  ) : (
                    <>
                      <FiSend size={15} /> Post experience
                    </>
                  )}
                </GradientButton>
              </form>
            </motion.div>
          </div>
        </>
      )}
    </DashboardLayout>
  );
}
