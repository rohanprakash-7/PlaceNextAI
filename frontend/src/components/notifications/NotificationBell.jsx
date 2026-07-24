import { useCallback, useEffect, useRef, useState } from "react";
import { AnimatePresence, motion } from "framer-motion";
import { useNavigate } from "react-router-dom";
import { FiBell, FiCheck, FiInbox } from "react-icons/fi";
import { useAuth, ROLE_HOME } from "../../context/AuthContext.jsx";
import { useToast } from "../../context/ToastContext.jsx";
import {
  getNotifications,
  getUnreadCount,
  markNotificationRead,
  markAllNotificationsRead,
} from "../../services/notificationService";

const POLL_INTERVAL_MS = 20000;

function canUseBrowserNotifications() {
  return typeof window !== "undefined" && "Notification" in window;
}

export default function NotificationBell() {
  const { user } = useAuth();
  const navigate = useNavigate();
  const toast = useToast();

  const [open, setOpen] = useState(false);
  const [items, setItems] = useState([]);
  const [unreadCount, setUnreadCount] = useState(0);
  const [pushPermission, setPushPermission] = useState(
    canUseBrowserNotifications() ? Notification.permission : "unsupported"
  );
  const containerRef = useRef(null);
  const lastSeenIdRef = useRef(null);
  const firstLoadRef = useRef(true);

  const poll = useCallback(async () => {
    try {
      const [list, count] = await Promise.all([getNotifications(), getUnreadCount()]);

      if (canUseBrowserNotifications() && Notification.permission === "granted") {
        const newest = list[0];
        if (!firstLoadRef.current && newest && newest.id !== lastSeenIdRef.current && !newest.read) {
          new Notification(newest.title, { body: newest.message });
        }
      }
      if (list[0]) {
        lastSeenIdRef.current = list[0].id;
      }
      firstLoadRef.current = false;

      setItems(list.slice(0, 8));
      setUnreadCount(count);
    } catch {
      // Silent - the bell should never throw errors into the rest of the app.
    }
  }, []);

  useEffect(() => {
    poll();
    const timer = setInterval(poll, POLL_INTERVAL_MS);
    return () => clearInterval(timer);
  }, [poll]);

  useEffect(() => {
    const onClickOutside = (event) => {
      if (containerRef.current && !containerRef.current.contains(event.target)) {
        setOpen(false);
      }
    };
    document.addEventListener("mousedown", onClickOutside);
    return () => document.removeEventListener("mousedown", onClickOutside);
  }, []);

  const handleEnablePush = async () => {
    if (!canUseBrowserNotifications()) return;
    const result = await Notification.requestPermission();
    setPushPermission(result);
  };

  const handleItemClick = async (item) => {
    if (!item.read) {
      try {
        await markNotificationRead(item.id);
        setItems((prev) => prev.map((entry) => (entry.id === item.id ? { ...entry, read: true } : entry)));
        setUnreadCount((count) => Math.max(0, count - 1));
      } catch (err) {
        toast.error(err.friendlyMessage || "Could not mark that notification as read");
      }
    }
    setOpen(false);
    if (item.link) {
      navigate(item.link);
    }
  };

  const handleMarkAllRead = async () => {
    try {
      await markAllNotificationsRead();
      setItems((prev) => prev.map((entry) => ({ ...entry, read: true })));
      setUnreadCount(0);
    } catch (err) {
      toast.error(err.friendlyMessage || "Could not mark all notifications as read");
    }
  };

  const viewAllPath = (ROLE_HOME[user?.role] || "/dashboard") + "/notifications";

  return (
    <div className="relative" ref={containerRef}>
      <button
        type="button"
        aria-label="Notifications"
        onClick={() => setOpen((value) => !value)}
        className="glass relative flex h-9 w-9 items-center justify-center rounded-xl text-slate-600 transition-colors hover:text-slate-900 dark:text-slate-300 dark:hover:text-white"
      >
        <FiBell size={16} />
        {unreadCount > 0 && (
          <span className="absolute -right-1 -top-1 flex h-4 min-w-[16px] items-center justify-center rounded-full bg-rose-500 px-1 text-[9px] font-bold text-white">
            {unreadCount > 9 ? "9+" : unreadCount}
          </span>
        )}
      </button>

      <AnimatePresence>
        {open && (
          <motion.div
            initial={{ opacity: 0, y: -8, scale: 0.97 }}
            animate={{ opacity: 1, y: 0, scale: 1 }}
            exit={{ opacity: 0, y: -8, scale: 0.97 }}
            transition={{ duration: 0.18 }}
            className="glass-strong absolute right-0 top-12 z-50 w-80 overflow-hidden rounded-2xl border border-slate-200 dark:border-white/10"
          >
            <div className="flex items-center justify-between border-b border-slate-200 px-4 py-3 dark:border-white/5">
              <p className="font-display text-sm font-semibold text-slate-900 dark:text-white">Notifications</p>
              {unreadCount > 0 && (
                <button
                  type="button"
                  onClick={handleMarkAllRead}
                  className="inline-flex items-center gap-1 text-[11px] font-medium text-primary-500 hover:text-primary-400"
                >
                  <FiCheck size={11} /> Mark all read
                </button>
              )}
            </div>

            {pushPermission === "default" && (
              <button
                type="button"
                onClick={handleEnablePush}
                className="w-full border-b border-slate-200 px-4 py-2.5 text-left text-[11px] text-primary-500 hover:bg-slate-50 dark:border-white/5 dark:hover:bg-white/[0.03]"
              >
                Enable desktop alerts for new notifications
              </button>
            )}

            <div className="max-h-80 overflow-y-auto">
              {items.length === 0 && (
                <div className="flex flex-col items-center gap-2 px-4 py-10 text-center">
                  <FiInbox className="text-slate-400" size={20} />
                  <p className="text-xs text-slate-500">You're all caught up.</p>
                </div>
              )}
              {items.map((item) => (
                <button
                  key={item.id}
                  type="button"
                  onClick={() => handleItemClick(item)}
                  className={
                    "block w-full border-b border-slate-100 px-4 py-3 text-left transition-colors last:border-0 hover:bg-slate-50 dark:border-white/5 dark:hover:bg-white/[0.03] " +
                    (item.read ? "" : "bg-primary-500/5")
                  }
                >
                  <div className="flex items-start gap-2">
                    {!item.read && <span className="mt-1.5 h-1.5 w-1.5 shrink-0 rounded-full bg-primary-500" />}
                    <div className={item.read ? "pl-3.5" : ""}>
                      <p className="text-xs font-semibold text-slate-900 dark:text-white">{item.title}</p>
                      <p className="mt-0.5 line-clamp-2 text-[11px] text-slate-500">{item.message}</p>
                      <p className="mt-1 text-[10px] text-slate-400 dark:text-slate-600">
                        {new Date(item.createdAt).toLocaleString()}
                      </p>
                    </div>
                  </div>
                </button>
              ))}
            </div>

            <button
              type="button"
              onClick={() => {
                setOpen(false);
                navigate(viewAllPath);
              }}
              className="block w-full border-t border-slate-200 px-4 py-2.5 text-center text-xs font-medium text-primary-500 hover:bg-slate-50 dark:border-white/5 dark:hover:bg-white/[0.03]"
            >
              View all
            </button>
          </motion.div>
        )}
      </AnimatePresence>
    </div>
  );
}
