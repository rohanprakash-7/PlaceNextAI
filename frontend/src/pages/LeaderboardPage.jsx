import { useCallback, useEffect, useState } from "react";
import { motion } from "framer-motion";
import { FiAward } from "react-icons/fi";
import DashboardLayout from "../components/dashboard/DashboardLayout.jsx";
import ErrorState from "../components/ui/ErrorState.jsx";
import EmptyState from "../components/ui/EmptyState.jsx";
import { SkeletonBlock } from "../components/ui/Skeleton.jsx";
import { STUDENT_NAV } from "../constants";
import { useAuth } from "../context/AuthContext.jsx";
import { getLeaderboard } from "../services/gamificationService";

const MEDAL_COLORS = {
  1: "text-amber-400",
  2: "text-slate-700 dark:text-slate-300",
  3: "text-orange-400",
};

export default function LeaderboardPage() {
  const { user } = useAuth();

  const [board, setBoard] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const load = useCallback(async () => {
    setLoading(true);
    setError("");
    try {
      setBoard(await getLeaderboard());
    } catch (err) {
      setError(err.friendlyMessage || "Failed to load the leaderboard");
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    load();
  }, [load]);

  const entries = board?.topEntries || [];
  const myEntryInTop = entries.some((entry) => entry.currentUser);

  return (
    <DashboardLayout navItems={STUDENT_NAV} roleLabel="Student" title="Leaderboard" userName={user?.name || "Student"}>
      {loading && <SkeletonBlock className="h-96" />}

      {!loading && error && (
        <div className="glass-card">
          <ErrorState message={error} onRetry={load} />
        </div>
      )}

      {!loading && !error && entries.length === 0 && (
        <div className="glass-card">
          <EmptyState
            icon={FiAward}
            title="No leaderboard activity yet"
            message="Earn XP by using the platform - resumes, mock interviews, applications and more - to appear here."
          />
        </div>
      )}

      {!loading && !error && entries.length > 0 && (
        <div className="glass-card overflow-hidden">
          <div className="overflow-x-auto">
            <table className="w-full min-w-[520px] text-left text-sm">
              <thead>
                <tr className="border-b border-slate-200 dark:border-white/5 text-xs uppercase tracking-wider text-slate-500">
                  <th className="px-6 py-3.5 font-medium">Rank</th>
                  <th className="px-6 py-3.5 font-medium">Student</th>
                  <th className="px-6 py-3.5 font-medium">Level</th>
                  <th className="px-6 py-3.5 font-medium">XP</th>
                </tr>
              </thead>
              <tbody>
                {entries.map((entry, index) => (
                  <motion.tr
                    key={entry.studentId}
                    initial={{ opacity: 0, x: -10 }}
                    animate={{ opacity: 1, x: 0 }}
                    transition={{ delay: index * 0.03 }}
                    className={
                      "border-b border-slate-200 dark:border-white/5 last:border-0 " +
                      (entry.currentUser ? "bg-primary-500/10" : "")
                    }
                  >
                    <td className="px-6 py-3.5">
                      <span className={"font-display text-base font-semibold " + (MEDAL_COLORS[entry.rank] || "text-slate-500 dark:text-slate-400")}>
                        #{entry.rank}
                      </span>
                    </td>
                    <td className="px-6 py-3.5">
                      <span className="font-medium text-slate-900 dark:text-white">{entry.fullName}</span>
                      {entry.currentUser && (
                        <span className="ml-2 rounded-full bg-primary-500/20 px-2 py-0.5 text-[10px] font-semibold text-primary-300">
                          You
                        </span>
                      )}
                      <p className="mt-0.5 text-xs text-slate-500">
                        {[entry.branch, entry.college].filter(Boolean).join(" · ")}
                      </p>
                    </td>
                    <td className="px-6 py-3.5 text-slate-700 dark:text-slate-300">Level {entry.level}</td>
                    <td className="px-6 py-3.5 font-semibold text-slate-900 dark:text-white">{entry.xp} XP</td>
                  </motion.tr>
                ))}

                {!myEntryInTop && board?.myEntry && (
                  <motion.tr initial={{ opacity: 0 }} animate={{ opacity: 1 }} className="border-t-2 border-primary-500/30 bg-primary-500/10">
                    <td className="px-6 py-3.5 font-display text-base font-semibold text-slate-500 dark:text-slate-400">
                      #{board.myEntry.rank}
                    </td>
                    <td className="px-6 py-3.5">
                      <span className="font-medium text-slate-900 dark:text-white">{board.myEntry.fullName}</span>
                      <span className="ml-2 rounded-full bg-primary-500/20 px-2 py-0.5 text-[10px] font-semibold text-primary-300">
                        You
                      </span>
                    </td>
                    <td className="px-6 py-3.5 text-slate-700 dark:text-slate-300">Level {board.myEntry.level}</td>
                    <td className="px-6 py-3.5 font-semibold text-slate-900 dark:text-white">{board.myEntry.xp} XP</td>
                  </motion.tr>
                )}
              </tbody>
            </table>
          </div>
        </div>
      )}
    </DashboardLayout>
  );
}
