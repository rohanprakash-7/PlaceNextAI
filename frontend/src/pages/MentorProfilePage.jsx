import { useCallback, useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { motion } from "framer-motion";
import {
  FiArrowLeft,
  FiStar,
  FiBookmark,
  FiLinkedin,
  FiBriefcase,
  FiSend,
  FiLoader,
  FiCalendar,
} from "react-icons/fi";
import DashboardLayout from "../components/dashboard/DashboardLayout.jsx";
import EmptyState from "../components/ui/EmptyState.jsx";
import ErrorState from "../components/ui/ErrorState.jsx";
import GradientButton from "../components/ui/GradientButton.jsx";
import Modal from "../components/ui/Modal.jsx.jsx";
import { SkeletonBlock } from "../components/ui/Skeleton.jsx";
import { STUDENT_NAV } from "../constants";
import { useAuth } from "../context/AuthContext.jsx";
import {
  bookMentorSession,
  getMentorProfile,
  sendMentorRequest,
  toggleMentorBookmark,
} from "../services/mentorService";

const TOPICS = ["Career Guidance", "Resume Review", "Interview Prep", "Roadmap Advice", "General"];

function StarRating({ rating }) {
  const rounded = Math.round(rating || 0);
  return (
    <span className="inline-flex items-center gap-0.5">
      {Array.from({ length: 5 }).map((_, index) => (
        <FiStar
          key={index}
          size={14}
          className={index < rounded ? "fill-amber-400 text-amber-400" : "text-slate-600"}
        />
      ))}
    </span>
  );
}

export default function MentorProfilePage() {
  const { alumniId } = useParams();
  const navigate = useNavigate();
  const { user } = useAuth();

  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [bookmarking, setBookmarking] = useState(false);
  const [bookingId, setBookingId] = useState(null);

  const [modalOpen, setModalOpen] = useState(false);
  const [form, setForm] = useState({ topic: TOPICS[0], message: "" });
  const [sending, setSending] = useState(false);
  const [sent, setSent] = useState(false);
  const [sendError, setSendError] = useState("");

  const load = useCallback(async () => {
    setLoading(true);
    setError("");
    try {
      setProfile(await getMentorProfile(alumniId));
    } catch (err) {
      setError(err.friendlyMessage || "Failed to load this mentor's profile");
    } finally {
      setLoading(false);
    }
  }, [alumniId]);

  useEffect(() => {
    load();
  }, [load]);

  const handleBookmark = async () => {
    setBookmarking(true);
    try {
      const { bookmarked } = await toggleMentorBookmark(alumniId);
      setProfile((prev) => (prev ? { ...prev, bookmarked } : prev));
    } catch (err) {
      setError(err.friendlyMessage || "Could not update bookmark");
    } finally {
      setBookmarking(false);
    }
  };

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

  const handleSendRequest = async (event) => {
    event.preventDefault();
    setSending(true);
    setSendError("");
    try {
      await sendMentorRequest({ alumniId: Number(alumniId), topic: form.topic, message: form.message });
      setSent(true);
      setForm({ topic: TOPICS[0], message: "" });
    } catch (err) {
      setSendError(err.friendlyMessage || "Could not send your request");
    } finally {
      setSending(false);
    }
  };

  return (
    <DashboardLayout navItems={STUDENT_NAV} roleLabel="Student" title="Mentor Profile" userName={user?.name || "Student"}>
      <button
        type="button"
        onClick={() => navigate("/dashboard/student/mentors")}
        className="mb-4 inline-flex items-center gap-2 text-xs font-medium text-slate-500 dark:text-slate-400 transition-colors hover:text-slate-900 dark:hover:text-white"
      >
        <FiArrowLeft size={14} /> Back to mentors
      </button>

      {loading && <SkeletonBlock className="h-96" />}

      {!loading && error && !profile && (
        <div className="glass-card">
          <ErrorState message={error} onRetry={load} />
        </div>
      )}

      {!loading && profile && (
        <div className="grid gap-5 lg:grid-cols-3">
          <motion.div initial={{ opacity: 0, y: 16 }} animate={{ opacity: 1, y: 0 }} className="glass-card p-6 lg:col-span-1">
            <div className="flex items-start justify-between">
              <div>
                <p className="font-display text-lg font-semibold text-slate-900 dark:text-white">{profile.fullName}</p>
                <p className="mt-1 flex items-center gap-1.5 text-xs text-slate-500">
                  <FiBriefcase size={12} />
                  {profile.designation} {profile.currentCompany ? "· " + profile.currentCompany : ""}
                </p>
              </div>
              <button
                type="button"
                onClick={handleBookmark}
                disabled={bookmarking}
                aria-label="Toggle bookmark"
                className={
                  "glass flex h-9 w-9 items-center justify-center rounded-xl transition-colors " +
                  (profile.bookmarked ? "text-amber-400" : "text-slate-500 dark:text-slate-400 hover:text-slate-900 dark:hover:text-white")
                }
              >
                <FiBookmark size={15} className={profile.bookmarked ? "fill-amber-400" : ""} />
              </button>
            </div>

            <div className="mt-3 flex items-center gap-2">
              <StarRating rating={profile.averageRating} />
              <span className="text-xs text-slate-500">
                {profile.averageRating ? profile.averageRating.toFixed(1) : "No ratings yet"}
                {profile.reviewCount ? " (" + profile.reviewCount + ")" : ""}
              </span>
            </div>

            {profile.bio && <p className="mt-4 text-sm leading-relaxed text-slate-500 dark:text-slate-400">{profile.bio}</p>}

            {profile.expertise && (
              <div className="mt-4 flex flex-wrap gap-1.5">
                {profile.expertise.split(",").map((skill) => (
                  <span
                    key={skill}
                    className="rounded-full bg-primary-500/10 px-2.5 py-1 text-[11px] font-medium text-primary-400"
                  >
                    {skill.trim()}
                  </span>
                ))}
              </div>
            )}

            <div className="mt-4 space-y-1.5 text-xs text-slate-500">
              {profile.graduationYear && <p>Class of {profile.graduationYear}</p>}
              {profile.yearsOfExperience != null && <p>{profile.yearsOfExperience} years of experience</p>}
              {profile.linkedinUrl && (
                <a
                  href={profile.linkedinUrl}
                  target="_blank"
                  rel="noreferrer"
                  className="inline-flex items-center gap-1.5 text-primary-400 hover:text-primary-300"
                >
                  <FiLinkedin size={13} /> LinkedIn profile
                </a>
              )}
            </div>

            <GradientButton onClick={() => setModalOpen(true)} className="mt-5 w-full">
              <FiSend size={14} /> Request mentorship
            </GradientButton>
          </motion.div>

          <div className="space-y-5 lg:col-span-2">
            <motion.div
              initial={{ opacity: 0, y: 16 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: 0.05 }}
              className="glass-card p-6"
            >
              <div className="flex items-center gap-2">
                <FiCalendar className="text-primary-400" size={16} />
                <h2 className="font-display text-lg font-semibold text-slate-900 dark:text-white">Open slots</h2>
              </div>
              <div className="mt-4 space-y-2">
                {profile.openSlots.length === 0 && (
                  <p className="text-xs text-slate-500">No open slots right now.</p>
                )}
                {profile.openSlots.map((slot) => (
                  <div
                    key={slot.id}
                    className="flex items-center justify-between rounded-lg border border-slate-200 dark:border-white/5 bg-slate-50 dark:bg-white/[0.03] px-3.5 py-2.5 text-xs"
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
            </motion.div>

            <motion.div
              initial={{ opacity: 0, y: 16 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: 0.1 }}
              className="glass-card p-6"
            >
              <h2 className="font-display text-lg font-semibold text-slate-900 dark:text-white">Reviews</h2>
              <div className="mt-4 space-y-3">
                {profile.recentReviews.length === 0 && (
                  <EmptyState title="No reviews yet" message="Be the first student to rate a session with this mentor." />
                )}
                {profile.recentReviews.map((review) => (
                  <div key={review.id} className="rounded-xl border border-slate-200 dark:border-white/5 bg-slate-50 dark:bg-white/[0.03] px-4 py-3.5">
                    <div className="flex items-center justify-between">
                      <p className="text-sm font-medium text-slate-900 dark:text-white">{review.studentName}</p>
                      <StarRating rating={review.rating} />
                    </div>
                    {review.comment && <p className="mt-1.5 text-sm text-slate-500 dark:text-slate-400">{review.comment}</p>}
                  </div>
                ))}
              </div>
            </motion.div>
          </div>
        </div>
      )}

      <Modal open={modalOpen} onClose={() => setModalOpen(false)} title="Request mentorship">
        {sent ? (
          <div className="py-4 text-center">
            <p className="text-sm text-slate-700 dark:text-slate-300">Your request has been sent. You'll be notified once it's accepted.</p>
            <GradientButton onClick={() => setModalOpen(false)} className="mt-4">
              Done
            </GradientButton>
          </div>
        ) : (
          <form onSubmit={handleSendRequest} className="space-y-4">
            {sendError && (
              <p className="rounded-lg border border-rose-500/30 bg-rose-500/10 px-3 py-2 text-xs text-rose-300">
                {sendError}
              </p>
            )}
            <div>
              <label className="mb-1.5 block text-xs font-medium text-slate-500 dark:text-slate-400">Topic</label>
              <select
                value={form.topic}
                onChange={(event) => setForm({ ...form, topic: event.target.value })}
                className="input-glass"
              >
                {TOPICS.map((topic) => (
                  <option key={topic} value={topic}>
                    {topic}
                  </option>
                ))}
              </select>
            </div>
            <div>
              <label className="mb-1.5 block text-xs font-medium text-slate-500 dark:text-slate-400">Message</label>
              <textarea
                required
                rows={4}
                value={form.message}
                onChange={(event) => setForm({ ...form, message: event.target.value })}
                placeholder="Tell them what you'd like guidance on..."
                className="input-glass resize-none"
              />
            </div>
            <GradientButton type="submit" disabled={sending} className="w-full">
              {sending ? (
                <>
                  <FiLoader className="animate-spin" size={16} /> Sending…
                </>
              ) : (
                <>
                  <FiSend size={15} /> Send request
                </>
              )}
            </GradientButton>
          </form>
        )}
      </Modal>
    </DashboardLayout>
  );
}
