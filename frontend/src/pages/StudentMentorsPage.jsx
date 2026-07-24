import { useCallback, useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { motion } from "framer-motion";
import { FiAward, FiDownload, FiLoader, FiSearch, FiBookmark, FiStar, FiSend } from "react-icons/fi";
import DashboardLayout from "../components/dashboard/DashboardLayout.jsx";
import EmptyState from "../components/ui/EmptyState.jsx";
import ErrorState from "../components/ui/ErrorState.jsx";
import GradientButton from "../components/ui/GradientButton.jsx";
import Modal from "../components/ui/Modal.jsx.jsx";
import { SkeletonBlock } from "../components/ui/Skeleton.jsx";
import { STUDENT_NAV } from "../constants";
import { useAuth } from "../context/AuthContext.jsx";
import {
  browseMentors,
  bookMentorSession,
  getMentorCompanies,
  getMyMentorSessions,
  downloadCalendarInvite,
  submitMentorReview,
  toggleMentorBookmark,
} from "../services/mentorService";

export default function StudentMentorsPage() {
  const { user } = useAuth();

  const [mentors, setMentors] = useState([]);
  const [companies, setCompanies] = useState([]);
  const [sessions, setSessions] = useState([]);
  const [search, setSearch] = useState("");
  const [company, setCompany] = useState("");
  const [bookmarkedOnly, setBookmarkedOnly] = useState(false);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [bookingId, setBookingId] = useState(null);
  const [bookmarkingId, setBookmarkingId] = useState(null);

  const [ratingSession, setRatingSession] = useState(null);
  const [ratingValue, setRatingValue] = useState(5);
  const [ratingComment, setRatingComment] = useState("");
  const [ratingSaving, setRatingSaving] = useState(false);
  const [ratingError, setRatingError] = useState("");

  const load = useCallback(async (filters) => {
    setLoading(true);
    setError("");
    try {
      const [mentorData, sessionData, companyData] = await Promise.all([
        browseMentors(filters),
        getMyMentorSessions(),
        getMentorCompanies(),
      ]);
      setMentors(mentorData);
      setSessions(sessionData);
      setCompanies(companyData);
    } catch (err) {
      setError(err.friendlyMessage || "Failed to load mentors");
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    load({});
  }, [load]);

  const handleFilter = (event) => {
    event.preventDefault();
    load({ search, company });
  };

  const handleBook = async (slotId) => {
    setBookingId(slotId);
    try {
      await bookMentorSession(slotId);
      await load({ search, company });
    } catch (err) {
      setError(err.friendlyMessage || "Could not book that slot");
    } finally {
      setBookingId(null);
    }
  };

  const handleBookmark = async (alumniId) => {
    setBookmarkingId(alumniId);
    try {
      const { bookmarked } = await toggleMentorBookmark(alumniId);
      setMentors((prev) =>
        prev.map((mentor) => (mentor.alumniId === alumniId ? { ...mentor, bookmarked } : mentor))
      );
    } catch (err) {
      setError(err.friendlyMessage || "Could not update bookmark");
    } finally {
      setBookmarkingId(null);
    }
  };

  const visibleMentors = bookmarkedOnly ? mentors.filter((mentor) => mentor.bookmarked) : mentors;

  const openRatingModal = (session) => {
    setRatingSession(session);
    setRatingValue(5);
    setRatingComment("");
    setRatingError("");
  };

  const handleSubmitRating = async (event) => {
    event.preventDefault();
    setRatingSaving(true);
    setRatingError("");
    try {
      await submitMentorReview({ slotId: ratingSession.id, rating: ratingValue, comment: ratingComment });
      setRatingSession(null);
    } catch (err) {
      setRatingError(err.friendlyMessage || "Could not submit your rating");
    } finally {
      setRatingSaving(false);
    }
  };

  return (
    <DashboardLayout navItems={STUDENT_NAV} roleLabel="Student" title="Alumni Mentors" userName={user?.name || "Student"}>
      <form onSubmit={handleFilter} className="glass-card flex flex-wrap items-center gap-3 p-4">
        <div className="relative flex-1 min-w-[220px]">
          <FiSearch className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-500" size={15} />
          <input
            type="text"
            value={search}
            onChange={(event) => setSearch(event.target.value)}
            placeholder="Search by name or expertise…"
            className="input-glass pl-10"
          />
        </div>
        <select
          value={company}
          onChange={(event) => setCompany(event.target.value)}
          className="input-glass w-auto min-w-[160px]"
        >
          <option value="">All companies</option>
          {companies.map((name) => (
            <option key={name} value={name}>
              {name}
            </option>
          ))}
        </select>
        <button
          type="button"
          onClick={() => setBookmarkedOnly((value) => !value)}
          className={
            "glass inline-flex items-center gap-1.5 rounded-xl px-3.5 py-2.5 text-xs font-medium transition-colors " +
            (bookmarkedOnly ? "text-amber-400" : "text-slate-700 dark:text-slate-300 hover:text-slate-900 dark:hover:text-white")
          }
        >
          <FiBookmark size={13} className={bookmarkedOnly ? "fill-amber-400" : ""} /> Bookmarked
        </button>
        <button
          type="submit"
          className="rounded-xl bg-brand-gradient px-4 py-2.5 text-sm font-semibold text-slate-900 dark:text-white shadow-glow-sm"
        >
          Search
        </button>
      </form>

      {loading && (
        <div className="mt-5 grid gap-5 lg:grid-cols-2">
          <SkeletonBlock className="h-64" />
          <SkeletonBlock className="h-64" />
        </div>
      )}

      {!loading && error && mentors.length === 0 && (
        <div className="glass-card mt-5">
          <ErrorState message={error} onRetry={() => load({ search, company })} />
        </div>
      )}

      {!loading && (
        <>
          {sessions.length > 0 && (
            <div className="glass-card my-5 p-6">
              <h2 className="font-display text-lg font-semibold text-slate-900 dark:text-white">My booked sessions</h2>
              <div className="mt-4 space-y-3">
                {sessions.map((session) => (
                  <div
                    key={session.id}
                    className="flex flex-wrap items-center justify-between gap-3 rounded-xl border border-slate-200 dark:border-white/5 bg-slate-50 dark:bg-white/[0.03] px-4 py-3.5"
                  >
                    <div>
                      <p className="text-sm font-medium text-slate-900 dark:text-white">
                        {session.alumniName} · {session.alumniCompany}
                      </p>
                      <p className="mt-0.5 text-xs text-slate-500">
                        {new Date(session.startTime).toLocaleString()}
                      </p>
                    </div>
                    <div className="flex items-center gap-2">
                      {new Date(session.endTime) < new Date() && (
                        <button
                          type="button"
                          onClick={() => openRatingModal(session)}
                          className="glass inline-flex items-center gap-2 rounded-xl px-3 py-2 text-xs font-medium text-slate-700 dark:text-slate-300 transition-colors hover:text-slate-900 dark:hover:text-white"
                        >
                          <FiStar size={13} /> Rate session
                        </button>
                      )}
                      <button
                        type="button"
                        onClick={() => downloadCalendarInvite(session.id)}
                        className="glass inline-flex items-center gap-2 rounded-xl px-3 py-2 text-xs font-medium text-slate-700 dark:text-slate-300 transition-colors hover:text-slate-900 dark:hover:text-white"
                      >
                        <FiDownload size={13} /> Add to calendar
                      </button>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          )}

          <div className="mt-5 grid gap-4 sm:grid-cols-2">
            {visibleMentors.length === 0 && (
              <div className="glass-card sm:col-span-2">
                <EmptyState
                  icon={FiAward}
                  title="No mentors found"
                  message="Try a different search term or clear the company filter."
                />
              </div>
            )}

            {visibleMentors.map((mentor, index) => (
              <motion.div
                key={mentor.alumniId}
                initial={{ opacity: 0, y: 14 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ delay: index * 0.06 }}
                className="glass-card p-5"
              >
                <div className="flex items-start justify-between">
                  <Link to={"/dashboard/student/mentors/" + mentor.alumniId} className="group">
                    <p className="font-display text-base font-semibold text-slate-900 dark:text-white group-hover:text-primary-400">
                      {mentor.fullName}
                    </p>
                    <p className="text-xs text-slate-500">
                      {mentor.designation} {mentor.currentCompany ? "· " + mentor.currentCompany : ""}
                    </p>
                  </Link>
                  <button
                    type="button"
                    onClick={() => handleBookmark(mentor.alumniId)}
                    disabled={bookmarkingId === mentor.alumniId}
                    aria-label="Toggle bookmark"
                    className={
                      "flex h-8 w-8 shrink-0 items-center justify-center rounded-lg transition-colors " +
                      (mentor.bookmarked ? "text-amber-400" : "text-slate-500 hover:text-slate-900 dark:hover:text-white")
                    }
                  >
                    <FiBookmark size={15} className={mentor.bookmarked ? "fill-amber-400" : ""} />
                  </button>
                </div>

                <div className="mt-2 flex items-center gap-1.5 text-xs text-slate-500">
                  <FiStar size={12} className={mentor.averageRating ? "fill-amber-400 text-amber-400" : ""} />
                  {mentor.averageRating ? mentor.averageRating.toFixed(1) : "No ratings yet"}
                  {mentor.reviewCount ? " (" + mentor.reviewCount + ")" : ""}
                </div>

                {mentor.bio && <p className="mt-2 text-sm text-slate-500 dark:text-slate-400">{mentor.bio}</p>}
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
                  {mentor.openSlots.slice(0, 3).map((slot) => (
                    <div
                      key={slot.id}
                      className="flex items-center justify-between rounded-lg border border-slate-200 dark:border-white/5 bg-slate-50 dark:bg-white/[0.03] px-3 py-2 text-xs"
                    >
                      <span className="text-slate-700 dark:text-slate-300">{new Date(slot.startTime).toLocaleString()}</span>
                      <button
                        type="button"
                        onClick={() => handleBook(slot.id)}
                        disabled={bookingId === slot.id}
                        className="rounded-lg bg-brand-gradient px-2.5 py-1.5 text-[11px] font-semibold text-slate-900 dark:text-white disabled:opacity-50"
                      >
                        {bookingId === slot.id ? <FiLoader className="animate-spin" size={12} /> : "Book"}
                      </button>
                    </div>
                  ))}
                </div>

                <Link
                  to={"/dashboard/student/mentors/" + mentor.alumniId}
                  className="mt-4 inline-block text-xs font-medium text-primary-400 hover:text-primary-300"
                >
                  View full profile →
                </Link>
              </motion.div>
            ))}
          </div>
        </>
      )}

      <Modal open={Boolean(ratingSession)} onClose={() => setRatingSession(null)} title="Rate this session">
        {ratingSession && (
          <form onSubmit={handleSubmitRating} className="space-y-4">
            {ratingError && (
              <p className="rounded-lg border border-rose-500/30 bg-rose-500/10 px-3 py-2 text-xs text-rose-300">
                {ratingError}
              </p>
            )}
            <div>
              <label className="mb-1.5 block text-xs font-medium text-slate-500 dark:text-slate-400">Rating</label>
              <div className="flex items-center gap-1.5">
                {[1, 2, 3, 4, 5].map((value) => (
                  <button
                    key={value}
                    type="button"
                    onClick={() => setRatingValue(value)}
                    aria-label={value + " stars"}
                    className="p-0.5"
                  >
                    <FiStar
                      size={22}
                      className={value <= ratingValue ? "fill-amber-400 text-amber-400" : "text-slate-600"}
                    />
                  </button>
                ))}
              </div>
            </div>
            <div>
              <label className="mb-1.5 block text-xs font-medium text-slate-500 dark:text-slate-400">Comment (optional)</label>
              <textarea
                rows={3}
                value={ratingComment}
                onChange={(event) => setRatingComment(event.target.value)}
                placeholder="How was your session?"
                className="input-glass resize-none"
              />
            </div>
            <GradientButton type="submit" disabled={ratingSaving} className="w-full">
              {ratingSaving ? (
                <>
                  <FiLoader className="animate-spin" size={16} /> Submitting…
                </>
              ) : (
                <>
                  <FiSend size={15} /> Submit rating
                </>
              )}
            </GradientButton>
          </form>
        )}
      </Modal>
    </DashboardLayout>
  );
}
