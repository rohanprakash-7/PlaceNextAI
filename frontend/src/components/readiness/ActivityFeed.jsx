import { motion } from "framer-motion";
import {
  FiLogIn,
  FiUser,
  FiFileText,
  FiSend,
  FiMic,
  FiCheckSquare,
  FiMessageSquare,
  FiActivity,
} from "react-icons/fi";

const EVENT_META = {
  LOGIN: { icon: FiLogIn, label: "Signed in" },
  PROFILE_UPDATED: { icon: FiUser, label: "Profile updated" },
  RESUME_UPLOADED: { icon: FiFileText, label: "Resume uploaded" },
  APPLICATION_SUBMITTED: { icon: FiSend, label: "Application submitted" },
  APPLICATION_STATUS_CHANGED: { icon: FiActivity, label: "Application status changed" },
  MOCK_INTERVIEW_COMPLETED: { icon: FiMic, label: "Mock interview completed" },
  ROADMAP_ITEM_COMPLETED: { icon: FiCheckSquare, label: "Roadmap item completed" },
  FEEDBACK_RECEIVED: { icon: FiMessageSquare, label: "Feedback received" },
};

function timeAgo(dateString) {
  const seconds = Math.floor((Date.now() - new Date(dateString).getTime()) / 1000);
  if (seconds < 60) return "just now";
  const minutes = Math.floor(seconds / 60);
  if (minutes < 60) return minutes + "m ago";
  const hours = Math.floor(minutes / 60);
  if (hours < 24) return hours + "h ago";
  return Math.floor(hours / 24) + "d ago";
}

export default function ActivityFeed({ events }) {
  return (
    <div className="glass-card p-6">
      <h2 className="font-display text-lg font-semibold text-slate-900 dark:text-white">Recent activity</h2>
      <p className="mt-1 text-xs text-slate-500">Every action here updates your readiness score.</p>

      {events.length === 0 ? (
        <p className="mt-6 text-sm text-slate-500">
          No activity yet — apply to a job or update your profile to get started.
        </p>
      ) : (
        <div className="mt-5 space-y-1">
          {events.slice(0, 8).map((event, index) => {
            const meta = EVENT_META[event.eventType] || EVENT_META.LOGIN;
            const Icon = meta.icon;
            return (
              <motion.div
                key={event.id}
                initial={{ opacity: 0, x: -10 }}
                animate={{ opacity: 1, x: 0 }}
                transition={{ duration: 0.35, delay: index * 0.06 }}
                className="flex items-start gap-3 rounded-xl px-3 py-2.5 transition-colors hover:bg-slate-50 dark:hover:bg-white/[0.03]"
              >
                <span className="mt-0.5 flex h-8 w-8 shrink-0 items-center justify-center rounded-lg bg-slate-100 dark:bg-white/[0.06] text-primary-400">
                  <Icon size={14} />
                </span>
                <div className="min-w-0">
                  <p className="text-sm text-slate-700 dark:text-slate-300">{event.payload || meta.label}</p>
                  <p className="mt-0.5 text-xs text-slate-500">{timeAgo(event.createdAt)}</p>
                </div>
              </motion.div>
            );
          })}
        </div>
      )}
    </div>
  );
}
