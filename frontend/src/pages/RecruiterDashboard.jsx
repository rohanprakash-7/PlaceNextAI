import { useCallback, useEffect, useState } from "react";
import { motion } from "framer-motion";
import { FiBriefcase, FiUsers, FiZap, FiCheckCircle, FiAward } from "react-icons/fi";
import DashboardLayout from "../components/dashboard/DashboardLayout.jsx";
import StatCard from "../components/ui/StatCard.jsx";
import BadgeShelf from "../components/badges/BadgeShelf.jsx";
import { RECRUITER_NAV } from "../constants";
import { useAuth } from "../context/AuthContext.jsx";
import { getMyRecruiterBadges } from "../services/badgeService";

const STATS = [
  { icon: FiBriefcase, label: "Active job postings", value: "8", trend: "+2", trendUp: true },
  { icon: FiUsers, label: "Total applicants", value: "1,247", trend: "+18%", trendUp: true },
  { icon: FiZap, label: "AI shortlisted", value: "96", trend: "+31", trendUp: true },
  { icon: FiCheckCircle, label: "Offers released", value: "12", trend: "+4", trendUp: true },
];

const CANDIDATES = [
  { name: "Rahul Verma", role: "Backend Engineer Intern", match: 96, status: "Shortlisted" },
  { name: "Priya Nair", role: "Data Analyst", match: 93, status: "Shortlisted" },
  { name: "Arjun Reddy", role: "Backend Engineer Intern", match: 89, status: "In review" },
  { name: "Sneha Iyer", role: "Frontend Developer", match: 87, status: "In review" },
  { name: "Karan Mehta", role: "Data Analyst", match: 82, status: "New" },
];

const STATUS_STYLES = {
  Shortlisted: "bg-emerald-500/10 text-emerald-400",
  "In review": "bg-amber-500/10 text-amber-400",
  New: "bg-accent-500/10 text-accent-400",
};

export default function RecruiterDashboard() {
  const { user } = useAuth();
  const [badges, setBadges] = useState([]);

  const loadBadges = useCallback(async () => {
    try {
      setBadges(await getMyRecruiterBadges());
    } catch {
      // Badges are a non-critical enhancement - fail silently on this dashboard.
    }
  }, []);

  useEffect(() => {
    loadBadges();
  }, [loadBadges]);

  return (
    <DashboardLayout
      navItems={RECRUITER_NAV}
      roleLabel="Recruiter"
      title="Recruiter Overview"
      userName={user?.name || "User"}
    >
      <div className="grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
        {STATS.map((stat, index) => (
          <StatCard key={stat.label} {...stat} delay={index * 0.07} />
        ))}
      </div>

      {badges.some((badge) => badge.earned) && (
        <motion.div initial={{ opacity: 0, y: 18 }} animate={{ opacity: 1, y: 0 }} className="glass-card mt-6 p-6">
          <div className="flex items-center gap-2">
            <FiAward className="text-primary-400" size={16} />
            <h2 className="font-display text-lg font-semibold text-slate-900 dark:text-white">Recruiter badges</h2>
          </div>
          <div className="mt-4">
            <BadgeShelf badges={badges} allowCertificates={false} />
          </div>
        </motion.div>
      )}

      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.5, delay: 0.3 }}
        className="glass-card mt-6 overflow-hidden"
      >
        <div className="flex items-center justify-between border-b border-slate-200 dark:border-white/5 px-6 py-5">
          <h2 className="font-display text-lg font-semibold text-slate-900 dark:text-white">Top AI-ranked candidates</h2>
          <span className="glass rounded-full px-3 py-1 text-xs font-medium text-primary-400">
            Live ranking
          </span>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full min-w-[640px] text-left text-sm">
            <thead>
              <tr className="border-b border-slate-200 dark:border-white/5 text-xs uppercase tracking-wider text-slate-500">
                <th className="px-6 py-3.5 font-medium">Candidate</th>
                <th className="px-6 py-3.5 font-medium">Applied role</th>
                <th className="px-6 py-3.5 font-medium">AI match</th>
                <th className="px-6 py-3.5 font-medium">Status</th>
              </tr>
            </thead>
            <tbody>
              {CANDIDATES.map((candidate, index) => (
                <motion.tr
                  key={candidate.name}
                  initial={{ opacity: 0, x: -12 }}
                  animate={{ opacity: 1, x: 0 }}
                  transition={{ duration: 0.4, delay: 0.4 + index * 0.08 }}
                  className="border-b border-slate-200 dark:border-white/5 transition-colors last:border-0 hover:bg-slate-50 dark:hover:bg-white/[0.03]"
                >
                  <td className="px-6 py-4">
                    <div className="flex items-center gap-3">
                      <span className="flex h-8 w-8 items-center justify-center rounded-lg bg-brand-gradient text-[11px] font-bold text-slate-900 dark:text-white">
                        {candidate.name
                          .split(" ")
                          .map((part) => part[0])
                          .join("")}
                      </span>
                      <span className="font-medium text-slate-900 dark:text-white">{candidate.name}</span>
                    </div>
                  </td>
                  <td className="px-6 py-4 text-slate-500 dark:text-slate-400">{candidate.role}</td>
                  <td className="px-6 py-4">
                    <div className="flex items-center gap-3">
                      <div className="h-1.5 w-20 overflow-hidden rounded-full bg-white/5">
                        <div
                          className="h-full rounded-full bg-brand-gradient"
                          style={{ width: candidate.match + "%" }}
                        />
                      </div>
                      <span className="font-semibold text-slate-900 dark:text-white">{candidate.match}%</span>
                    </div>
                  </td>
                  <td className="px-6 py-4">
                    <span
                      className={
                        "rounded-full px-2.5 py-1 text-xs font-semibold " +
                        STATUS_STYLES[candidate.status]
                      }
                    >
                      {candidate.status}
                    </span>
                  </td>
                </motion.tr>
              ))}
            </tbody>
          </table>
        </div>
      </motion.div>
    </DashboardLayout>
  );
}
