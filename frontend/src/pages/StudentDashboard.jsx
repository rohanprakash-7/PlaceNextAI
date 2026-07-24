import { useCallback, useEffect, useState } from "react";
import { motion } from "framer-motion";
import { FiClock, FiUser, FiAward, FiBarChart2 } from "react-icons/fi";
import { Link } from "react-router-dom";
import DashboardLayout from "../components/dashboard/DashboardLayout.jsx";
import ReadinessCard from "../components/readiness/ReadinessCard.jsx";
import ActivityFeed from "../components/readiness/ActivityFeed.jsx";
import SkillRadarChart from "../components/visualizations/SkillRadarChart.jsx";
import WeeklyActivityHeatmap from "../components/visualizations/WeeklyActivityHeatmap.jsx";
import BadgeShelf from "../components/badges/BadgeShelf.jsx";
import XpBar from "../components/badges/XpBar.jsx";
import { SkeletonBlock } from "../components/ui/Skeleton.jsx";
import ErrorState from "../components/ui/ErrorState.jsx";
import { STUDENT_NAV } from "../constants";
import { useAuth } from "../context/AuthContext.jsx";
import {
  getReadiness,
  recomputeReadiness,
  getRecentEvents,
  getActivityHeatmap,
} from "../services/readinessService";
import { getMyBadges } from "../services/badgeService";
import { getMyGamificationSummary } from "../services/gamificationService";

const ROLE_LABELS = {
  ROLE_STUDENT: "Student",
  ROLE_RECRUITER: "Recruiter",
  ROLE_ADMIN: "Admin",
};

const UPCOMING = [
  { title: "Mock interview — Backend role", time: "Today, 5:00 PM", tag: "Interview" },
  { title: "Resume review — v3 draft", time: "Tomorrow, 11:00 AM", tag: "Resume" },
  { title: "Aptitude practice sprint", time: "Wed, 4:30 PM", tag: "Practice" },
];

export default function StudentDashboard() {
  const { user } = useAuth();

  const [readiness, setReadiness] = useState(null);
  const [events, setEvents] = useState([]);
  const [heatmap, setHeatmap] = useState([]);
  const [badges, setBadges] = useState([]);
  const [gamification, setGamification] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [recomputing, setRecomputing] = useState(false);

  const load = useCallback(async () => {
    setLoading(true);
    setError("");
    try {
      const [readinessData, eventsData, heatmapData, badgeData, gamificationData] = await Promise.all([
        getReadiness(),
        getRecentEvents(),
        getActivityHeatmap(90),
        getMyBadges(),
        getMyGamificationSummary(),
      ]);
      setReadiness(readinessData);
      setEvents(eventsData);
      setHeatmap(heatmapData);
      setBadges(badgeData);
      setGamification(gamificationData);
    } catch (err) {
      setError(err.friendlyMessage || "Failed to load your readiness data");
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    load();
  }, [load]);

  const handleRecompute = async () => {
    setRecomputing(true);
    try {
      const updated = await recomputeReadiness();
      setReadiness(updated);
      const eventsData = await getRecentEvents();
      setEvents(eventsData);
    } catch (err) {
      setError(err.friendlyMessage || "Recompute failed");
    } finally {
      setRecomputing(false);
    }
  };

  const completion = user?.profileCompletion ?? 0;
  const initials = (user?.name || "S")
    .split(" ")
    .map((part) => part[0])
    .join("")
    .slice(0, 2)
    .toUpperCase();

  return (
    <DashboardLayout
      navItems={STUDENT_NAV}
      roleLabel="Student"
      title="Student Overview"
      userName={user?.name || "Student"}
    >
      <motion.div
        initial={{ opacity: 0, y: 18 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.5 }}
        className="glass-card mb-6 flex flex-col gap-5 p-6 sm:flex-row sm:items-center sm:justify-between"
      >
        <div className="flex items-center gap-4">
          <span className="flex h-14 w-14 items-center justify-center rounded-2xl bg-brand-gradient font-display text-lg font-bold text-slate-900 dark:text-white shadow-glow-sm">
            {initials}
          </span>
          <div>
            <h2 className="font-display text-xl font-semibold text-slate-900 dark:text-white">{user?.name}</h2>
            <p className="mt-0.5 text-sm text-slate-500 dark:text-slate-400">{user?.email}</p>
            <span className="mt-2 inline-flex items-center gap-1.5 rounded-full bg-primary-500/10 px-2.5 py-1 text-xs font-semibold text-primary-400">
              <FiUser size={12} /> {ROLE_LABELS[user?.role] || user?.role}
            </span>
          </div>
        </div>

        <div className="flex flex-col gap-4 sm:items-end">
          <div className="w-full sm:w-64">
            <div className="flex items-center justify-between text-sm">
              <span className="text-slate-500 dark:text-slate-400">Profile completion</span>
              <span className="font-semibold text-slate-900 dark:text-white">{completion}%</span>
            </div>
            <div className="mt-2 h-2 overflow-hidden rounded-full bg-white/5">
              <motion.div
                initial={{ width: 0 }}
                animate={{ width: completion + "%" }}
                transition={{ duration: 1, delay: 0.3, ease: "easeOut" }}
                className="h-full rounded-full bg-brand-gradient"
              />
            </div>
          </div>
          <XpBar summary={gamification} />
        </div>
      </motion.div>

      {loading && (
        <div className="grid gap-5 lg:grid-cols-3">
          <SkeletonBlock className="h-80 lg:col-span-2" />
          <SkeletonBlock className="h-80" />
        </div>
      )}

      {!loading && error && (
        <div className="glass-card">
          <ErrorState message={error} onRetry={load} />
        </div>
      )}

      {!loading && !error && readiness && (
        <div className="grid gap-5 lg:grid-cols-3">
          <div className="lg:col-span-2">
            <ReadinessCard
              readiness={readiness}
              onRecompute={handleRecompute}
              recomputing={recomputing}
            />
          </div>
          <ActivityFeed events={events} />
        </div>
      )}

      {!loading && !error && readiness && (
        <div className="mt-5 grid gap-5 lg:grid-cols-2">
          <motion.div initial={{ opacity: 0, y: 18 }} animate={{ opacity: 1, y: 0 }} className="glass-card p-6">
            <h2 className="font-display text-lg font-semibold text-slate-900 dark:text-white">Skill radar</h2>
            <p className="mt-1 text-xs text-slate-500">The same five dimensions, plotted for shape at a glance</p>
            <SkillRadarChart readiness={readiness} />
          </motion.div>

          <motion.div
            initial={{ opacity: 0, y: 18 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.1 }}
            className="glass-card p-6"
          >
            <h2 className="font-display text-lg font-semibold text-slate-900 dark:text-white">Activity, last 90 days</h2>
            <p className="mt-1 text-xs text-slate-500">Darker squares mean more platform activity that day</p>
            <div className="mt-4">
              <WeeklyActivityHeatmap counts={heatmap} days={90} />
            </div>
          </motion.div>
        </div>
      )}

      {!loading && !error && badges.length > 0 && (
        <motion.div
          initial={{ opacity: 0, y: 18 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.15 }}
          className="glass-card mt-5 p-6"
        >
          <div className="flex items-center justify-between">
            <div>
              <h2 className="font-display text-lg font-semibold text-slate-900 dark:text-white">Achievements</h2>
              <p className="mt-1 text-xs text-slate-500">Earned automatically as you use the platform</p>
            </div>
            <div className="flex items-center gap-2">
              <Link
                to="/dashboard/student/achievements"
                className="glass inline-flex items-center gap-1.5 rounded-xl px-3 py-2 text-xs font-medium text-slate-700 dark:text-slate-300 transition-colors hover:text-slate-900 dark:hover:text-white"
              >
                <FiAward size={13} /> View all
              </Link>
              <Link
                to="/dashboard/student/leaderboard"
                className="glass inline-flex items-center gap-1.5 rounded-xl px-3 py-2 text-xs font-medium text-slate-700 dark:text-slate-300 transition-colors hover:text-slate-900 dark:hover:text-white"
              >
                <FiBarChart2 size={13} /> Leaderboard
              </Link>
            </div>
          </div>
          <div className="mt-4">
            <BadgeShelf badges={badges} />
          </div>
        </motion.div>
      )}

      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.5, delay: 0.35 }}
        className="glass-card mt-5 p-6"
      >
        <h2 className="font-display text-lg font-semibold text-slate-900 dark:text-white">Upcoming sessions</h2>
        <div className="mt-5 grid gap-3 sm:grid-cols-3">
          {UPCOMING.map((session) => (
            <div
              key={session.title}
              className="rounded-xl border border-slate-200 dark:border-white/5 bg-slate-50 dark:bg-white/[0.03] p-4 transition-colors hover:border-primary-500/30"
            >
              <div className="flex items-center justify-between">
                <span className="rounded-full bg-primary-500/10 px-2.5 py-0.5 text-[11px] font-semibold text-primary-400">
                  {session.tag}
                </span>
                <FiClock className="text-slate-500" size={14} />
              </div>
              <p className="mt-2.5 text-sm font-medium text-slate-900 dark:text-white">{session.title}</p>
              <p className="mt-1 text-xs text-slate-500">{session.time}</p>
            </div>
          ))}
        </div>
      </motion.div>
    </DashboardLayout>
  );
}
