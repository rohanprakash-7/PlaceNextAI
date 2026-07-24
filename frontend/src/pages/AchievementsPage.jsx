import { useCallback, useEffect, useState } from "react";
import { motion } from "framer-motion";
import { FiAward, FiBarChart2 } from "react-icons/fi";
import { Link } from "react-router-dom";
import DashboardLayout from "../components/dashboard/DashboardLayout.jsx";
import ErrorState from "../components/ui/ErrorState.jsx";
import { SkeletonBlock } from "../components/ui/Skeleton.jsx";
import BadgeShelf from "../components/badges/BadgeShelf.jsx";
import XpBar from "../components/badges/XpBar.jsx";
import { STUDENT_NAV } from "../constants";
import { useAuth } from "../context/AuthContext.jsx";
import { getMyBadges } from "../services/badgeService";
import { getMyGamificationSummary } from "../services/gamificationService";

export default function AchievementsPage() {
  const { user } = useAuth();

  const [summary, setSummary] = useState(null);
  const [badges, setBadges] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const load = useCallback(async () => {
    setLoading(true);
    setError("");
    try {
      const [summaryData, badgeData] = await Promise.all([getMyGamificationSummary(), getMyBadges()]);
      setSummary(summaryData);
      setBadges(badgeData);
    } catch (err) {
      setError(err.friendlyMessage || "Failed to load your achievements");
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    load();
  }, [load]);

  const earnedCount = badges.filter((badge) => badge.earned).length;

  return (
    <DashboardLayout navItems={STUDENT_NAV} roleLabel="Student" title="Achievements" userName={user?.name || "Student"}>
      {loading && (
        <div className="grid gap-5">
          <SkeletonBlock className="h-32" />
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
          <motion.div
            initial={{ opacity: 0, y: 16 }}
            animate={{ opacity: 1, y: 0 }}
            className="glass-card flex flex-col gap-5 p-6 sm:flex-row sm:items-center sm:justify-between"
          >
            <div>
              <h2 className="font-display text-lg font-semibold text-slate-900 dark:text-white">Your progress</h2>
              <p className="mt-1 text-xs text-slate-500">
                {earnedCount} of {badges.length} badges earned · {summary?.xp ?? 0} total XP
              </p>
            </div>
            <div className="flex items-center gap-4">
              <XpBar summary={summary} />
              <Link
                to="/dashboard/student/leaderboard"
                className="glass inline-flex shrink-0 items-center gap-2 rounded-xl px-3.5 py-2.5 text-xs font-medium text-slate-700 dark:text-slate-300 transition-colors hover:text-slate-900 dark:hover:text-white"
              >
                <FiBarChart2 size={13} /> Leaderboard
              </Link>
            </div>
          </motion.div>

          <motion.div
            initial={{ opacity: 0, y: 16 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.1 }}
            className="glass-card mt-5 p-6"
          >
            <div className="flex items-center gap-2">
              <FiAward className="text-primary-400" size={16} />
              <h2 className="font-display text-lg font-semibold text-slate-900 dark:text-white">Badges</h2>
            </div>
            <p className="mt-1 text-xs text-slate-500">
              Hover an earned badge and tap the download icon for a certificate.
            </p>
            <div className="mt-5">
              <BadgeShelf badges={badges} />
            </div>
          </motion.div>
        </>
      )}
    </DashboardLayout>
  );
}
