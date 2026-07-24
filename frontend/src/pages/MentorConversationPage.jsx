import { useCallback, useEffect, useRef, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { motion } from "framer-motion";
import { FiArrowLeft, FiSend, FiLoader } from "react-icons/fi";
import DashboardLayout from "../components/dashboard/DashboardLayout.jsx";
import ErrorState from "../components/ui/ErrorState.jsx";
import { SkeletonBlock } from "../components/ui/Skeleton.jsx";
import { STUDENT_NAV, ALUMNI_NAV } from "../constants";
import { useAuth } from "../context/AuthContext.jsx";
import { getMentorMessages, getMentorRequest, sendMentorMessage } from "../services/mentorService";

const POLL_INTERVAL_MS = 5000;

export default function MentorConversationPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { user } = useAuth();
  const isAlumni = user?.role === "ROLE_ALUMNI";

  const [request, setRequest] = useState(null);
  const [messages, setMessages] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [draft, setDraft] = useState("");
  const [sending, setSending] = useState(false);
  const bottomRef = useRef(null);

  const load = useCallback(async () => {
    setError("");
    try {
      const [requestData, messageData] = await Promise.all([
        getMentorRequest(id),
        getMentorMessages(id),
      ]);
      setRequest(requestData);
      setMessages(messageData);
    } catch (err) {
      setError(err.friendlyMessage || "Failed to load this conversation");
    } finally {
      setLoading(false);
    }
  }, [id]);

  useEffect(() => {
    load();
    const timer = setInterval(async () => {
      try {
        setMessages(await getMentorMessages(id));
      } catch {
        // silent background refresh failure
      }
    }, POLL_INTERVAL_MS);
    return () => clearInterval(timer);
  }, [id, load]);

  useEffect(() => {
    bottomRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages]);

  const handleSend = async (event) => {
    event.preventDefault();
    if (!draft.trim()) return;
    setSending(true);
    try {
      const message = await sendMentorMessage(id, draft.trim());
      setMessages((prev) => [...prev, message]);
      setDraft("");
    } catch (err) {
      setError(err.friendlyMessage || "Could not send that message");
    } finally {
      setSending(false);
    }
  };

  const navItems = isAlumni ? ALUMNI_NAV : STUDENT_NAV;
  const backTo = isAlumni ? "/dashboard/alumni/requests" : "/dashboard/student/mentors/requests";

  return (
    <DashboardLayout navItems={navItems} roleLabel={isAlumni ? "Alumni" : "Student"} title="Conversation" userName={user?.name || "User"}>
      <button
        type="button"
        onClick={() => navigate(backTo)}
        className="mb-4 inline-flex items-center gap-2 text-xs font-medium text-slate-500 dark:text-slate-400 transition-colors hover:text-slate-900 dark:hover:text-white"
      >
        <FiArrowLeft size={14} /> Back to requests
      </button>

      {loading && <SkeletonBlock className="h-96" />}

      {!loading && error && messages.length === 0 && (
        <div className="glass-card">
          <ErrorState message={error} onRetry={load} />
        </div>
      )}

      {!loading && request && (
        <div className="glass-card flex h-[70vh] flex-col overflow-hidden">
          <div className="border-b border-slate-200 dark:border-white/5 px-6 py-4">
            <p className="font-display text-base font-semibold text-slate-900 dark:text-white">
              {isAlumni ? request.studentName : request.alumniName}
            </p>
            <p className="text-xs text-slate-500">
              {request.topic} {request.alumniCompany ? "· " + request.alumniCompany : ""}
            </p>
          </div>

          <div className="flex-1 space-y-3 overflow-y-auto px-6 py-4">
            {messages.length === 0 && (
              <p className="mt-10 text-center text-sm text-slate-500">
                No messages yet. Say hello and get the conversation started.
              </p>
            )}
            {messages.map((message, index) => {
              const mine = isAlumni ? message.senderRole === "ALUMNI" : message.senderRole === "STUDENT";
              return (
                <motion.div
                  key={message.id}
                  initial={{ opacity: 0, y: 8 }}
                  animate={{ opacity: 1, y: 0 }}
                  transition={{ delay: Math.min(index * 0.02, 0.3) }}
                  className={"flex " + (mine ? "justify-end" : "justify-start")}
                >
                  <div
                    className={
                      "max-w-[75%] rounded-2xl px-4 py-2.5 text-sm " +
                      (mine
                        ? "bg-brand-gradient text-slate-900 dark:text-white"
                        : "border border-slate-200 dark:border-white/5 bg-slate-50 dark:bg-white/[0.04] text-slate-700 dark:text-slate-200")
                    }
                  >
                    <p className="leading-relaxed">{message.content}</p>
                    <p className={"mt-1 text-[10px] " + (mine ? "text-white/70" : "text-slate-500")}>
                      {new Date(message.sentAt).toLocaleTimeString()}
                    </p>
                  </div>
                </motion.div>
              );
            })}
            <div ref={bottomRef} />
          </div>

          {request.status === "ACCEPTED" ? (
            <form onSubmit={handleSend} className="flex items-center gap-3 border-t border-slate-200 dark:border-white/5 px-4 py-3.5">
              <input
                type="text"
                value={draft}
                onChange={(event) => setDraft(event.target.value)}
                placeholder="Type a message…"
                className="input-glass flex-1"
              />
              <button
                type="submit"
                disabled={sending || !draft.trim()}
                className="inline-flex h-11 w-11 shrink-0 items-center justify-center rounded-xl bg-brand-gradient text-slate-900 dark:text-white shadow-glow-sm disabled:opacity-50"
              >
                {sending ? <FiLoader className="animate-spin" size={16} /> : <FiSend size={16} />}
              </button>
            </form>
          ) : (
            <div className="border-t border-slate-200 dark:border-white/5 px-6 py-4 text-center text-xs text-slate-500">
              Messaging opens once the mentorship request is accepted.
            </div>
          )}
        </div>
      )}
    </DashboardLayout>
  );
}
