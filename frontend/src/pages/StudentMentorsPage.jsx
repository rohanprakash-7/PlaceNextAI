import { useCallback, useEffect, useState } from "react";
import { motion } from "framer-motion";
import { FiAward, FiDownload, FiLoader } from "react-icons/fi";
import DashboardLayout from "../components/dashboard/DashboardLayout.jsx";
import EmptyState from "../components/ui/EmptyState.jsx";
import ErrorState from "../components/ui/ErrorState.jsx";
import { SkeletonBlock } from "../components/ui/Skeleton.jsx";
import { STUDENT_NAV } from "../constants";
import { useAuth } from "../context/AuthContext.jsx";
import {
  browseMentors,
  bookMentorSession,
  getMyMentorSessions,
  downloadCalendarInvite,
} from "../services/mentorService";

export default function StudentMentorsPage() {
  const { user } = useAuth();

  const [mentors, setMentors] = useState([]);
  const [sessions, setSessions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [bookingId, setBookingId] = useState(null);

  const load = useCallback(async () => {
    setLoading(true);
    setError("");
    try {
      const [mentorData, sessionData] = await Promise.all([browseMentors(), getMyMentorSessions()]);
      setMentors(mentorData);
      setSessions(sessionData);
    } catch (err) {
      setError(err.friendlyMessage || "Failed to load mentors");
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    load();
  }, [load]);

  const handleBook = async (slotId) => {
    setBookingId(slotId);
    try {
      await bookMentorSession(slotId);
      await load();
    } catch (err) {
      setError(err.friendlyMessage || "Could not book that slot");
    } finally {
      setBookingId(null);
    }
  };

  return (
    <DashboardLayout navItems={STUDENT_NAV} roleLabel="Student" title="Alumni Mentors" userName={user?.name || "Student"}>
      {loading && (
        <div className="grid gap-5 lg:grid-cols-2">
          <SkeletonBlock className="h-64" />
          <SkeletonBlock className="h-64" />
        </div>
      )}

      {!loading && error && mentors.length === 0 && (
        <div className="glass-card">
          <ErrorState message={error} onRetry={load} />
        </div>
      )}

      {!loading && (
        <>
          {sessions.length > 0 && (
            <div className="glass-card mb-5 p-6">
              <h2 className="font-display text-lg font-semibold text-white">My booked sessions</h2>
              <div className="mt-4 space-y-3">
                {sessions.map((session) => (
                  <div
                    key={session.id}
                    className="flex flex-wrap items-center justify-between gap-3 rounded-xl border border-white/5 bg-white/[0.03] px-4 py-3.5"
                  >
                    <div>
                      <p className="text-sm font-medium text-white">
                        {session.alumniName} · {session.alumniCompany}
                      </p>
                      <p className="mt-0.5 text-xs text-slate-500">
                        {new Date(session.startTime).toLocaleString()}
                      </p>
                    </div>
                    <button
                      type="button"
                      onClick={() => downloadCalendarInvite(session.id)}
                      className="glass inline-flex items-center gap-2 rounded-xl px-3 py-2 text-xs font-medium text-slate-300 transition-colors hover:text-white"
                    >
                      <FiDownload size={13} /> Add to calendar
                    </button>
                  </div>
                ))}
              </div>
            </div>
          )}

          <div className="grid gap-4 sm:grid-cols-2">
            {mentors.length === 0 && (
              <div className="glass-card sm:col-span-2">
                <EmptyState
                  icon={FiAward}
                  title="No mentors available yet"
                  message="Check back soon - alumni will publish availability for mentor sessions here."
                />
              </div>
            )}

            {mentors.map((mentor, index) => (
              <motion.div
                key={mentor.alumniId}
                initial={{ opacity: 0, y: 14 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ delay: index * 0.06 }}
                className="glass-card p-5"
              >
                <p className="font-display text-base font-semibold text-white">{mentor.fullName}</p>
                <p className="text-xs text-slate-500">
                  {mentor.designation} {mentor.currentCompany ? "· " + mentor.currentCompany : ""}
                </p>
                {mentor.bio && <p className="mt-2 text-sm text-slate-400">{mentor.bio}</p>}
                {mentor.expertise && (
                  <div className="mt-3 flex flex-wrap gap-1.5">
                    {mentor.expertise.split(",").map((skill) => (
                      <span
                        key={skill}
                        className="rounded-full bg-primary-500/10 px-2.5 py-1 text-[11px] font-medium text-primary-400"
                      >
                        {skill.trim()}
                      </span>
                    ))}
                  </div>
                )}

                <div className="mt-4 space-y-2">
                  {mentor.openSlots.length === 0 && (
                    <p className="text-xs text-slate-500">No open slots right now.</p>
                  )}
                  {mentor.openSlots.map((slot) => (
                    <div
                      key={slot.id}
                      className="flex items-center justify-between rounded-lg border border-white/5 bg-white/[0.03] px-3 py-2 text-xs"
                    >
                      <span className="text-slate-300">{new Date(slot.startTime).toLocaleString()}</span>
                      <button
                        type="button"
                        onClick={() => handleBook(slot.id)}
                        disabled={bookingId === slot.id}
                        className="rounded-lg bg-brand-gradient px-2.5 py-1.5 text-[11px] font-semibold text-white disabled:opacity-50"
                      >
                        {bookingId === slot.id ? <FiLoader className="animate-spin" size={12} /> : "Book"}
                      </button>
                    </div>
                  ))}
                </div>
              </motion.div>
            ))}
          </div>
        </>
      )}
    </DashboardLayout>
  );
}
