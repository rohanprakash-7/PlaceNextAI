import { useCallback, useEffect, useState } from "react";
import { motion } from "framer-motion";
import { useNavigate } from "react-router-dom";
import { FiBell, FiCheck } from "react-icons/fi";
import DashboardLayout from "../components/dashboard/DashboardLayout.jsx";
import EmptyState from "../components/ui/EmptyState.jsx";
import ErrorState from "../components/ui/ErrorState.jsx";
import { SkeletonBlock } from "../components/ui/Skeleton.jsx";
import { STUDENT_NAV, RECRUITER_NAV, ADMIN_NAV, ALUMNI_NAV } from "../constants";
import { useAuth } from "../context/AuthContext.jsx";
import {
  getNotifications,
  markNotificationRead,
  markAllNotificationsRead,
} from "../services/notificationService";

const NAV_BY_ROLE = {
  ROLE_STUDENT: { nav: STUDENT_NAV, label: "Student" },
  ROLE_RECRUITER: { nav: RECRUITER_NAV, label: "Recruiter" },
  ROLE_ADMIN: { nav: ADMIN_NAV, label: "Admin" },
  ROLE_ALUMNI: { nav: ALUMNI_NAV, label: "Alumni" },
};

export default function NotificationHistoryPage() {
  const { user } = useAuth();
  const navigate = useNavigate();
  const roleConfig = NAV_BY_ROLE[user?.role] || NAV_BY_ROLE.ROLE_STUDENT;

  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const load = useCallback(async () => {
    setLoading(true);
    setError("");
    try {
      setItems(await getNotifications());
    } catch (err) {
      setError(err.friendlyMessage || "Failed to load notifications");
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    load();
  }, [load]);

  const handleClick = async (item) => {
    if (!item.read) {
      try {
        await markNotificationRead(item.id);
        setItems((prev) => prev.map((entry) => (entry.id === item.id ? { ...entry, read: true } : entry)));
      } catch {
        // Non-fatal.
      }
    }
    if (item.link) {
      navigate(item.link);
    }
  };

  const handleMarkAllRead = async () => {
    try {
      await markAllNotificationsRead();
      setItems((prev) => prev.map((entry) => ({ ...entry, read: true })));
    } catch (err) {
      setError(err.friendlyMessage || "Could not mark all as read");
    }
  };

  const unreadCount = items.filter((item) => !item.read).length;

  return (
    <DashboardLayout navItems={roleConfig.nav} roleLabel={roleConfig.label} title="Notifications" userName={user?.name || "User"}>
      {loading && <SkeletonBlock className="h-96" />}

      {!loading && error && items.length === 0 && (
        <div className="glass-card">
          <ErrorState message={error} onRetry={load} />
        </div>
      )}

      {!loading && !error && items.length === 0 && (
        <div className="glass-card">
          <EmptyState
            icon={FiBell}
            title="No notifications yet"
            message="Application updates, mentor activity, job alerts and badges you earn will show up here."
          />
        </div>
      )}

      {!loading && items.length > 0 && (
        <div className="glass-card overflow-hidden">
          <div className="flex items-center justify-between border-b border-slate-200 px-6 py-4 dark:border-white/5">
            <p className="text-sm text-slate-500">
              {unreadCount > 0 ? unreadCount + " unread" : "All caught up"}
            </p>
            {unreadCount > 0 && (
              <button
                type="button"
                onClick={handleMarkAllRead}
                className="inline-flex items-center gap-1.5 text-xs font-medium text-primary-500 hover:text-primary-400"
              >
                <FiCheck size={13} /> Mark all read
              </button>
            )}
          </div>

          <div>
            {items.map((item, index) => (
              <motion.button
                key={item.id}
                type="button"
                initial={{ opacity: 0, y: 8 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ delay: Math.min(index * 0.02, 0.4) }}
                onClick={() => handleClick(item)}
                className={
                  "flex w-full items-start gap-3 border-b border-slate-100 px-6 py-4 text-left transition-colors last:border-0 hover:bg-slate-50 dark:border-white/5 dark:hover:bg-white/[0.03] " +
                  (item.read ? "" : "bg-primary-500/5")
                }
              >
                {!item.read && <span className="mt-1.5 h-2 w-2 shrink-0 rounded-full bg-primary-500" />}
                <div className={item.read ? "pl-5" : ""}>
                  <p className="text-sm font-semibold text-slate-900 dark:text-white">{item.title}</p>
                  <p className="mt-1 text-sm text-slate-500">{item.message}</p>
                  <p className="mt-1.5 text-xs text-slate-400 dark:text-slate-600">
                    {new Date(item.createdAt).toLocaleString()}
                  </p>
                </div>
              </motion.button>
            ))}
          </div>
        </div>
      )}
    </DashboardLayout>
  );
}
