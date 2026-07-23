import { useCallback, useEffect, useState } from "react";
import { motion } from "framer-motion";
import { FiBarChart2 } from "react-icons/fi";
import DashboardLayout from "../components/dashboard/DashboardLayout.jsx";
import FunnelChart from "../components/analytics/FunnelChart.jsx";
import SkillDistributionChart from "../components/analytics/SkillDistributionChart.jsx";
import ErrorState from "../components/ui/ErrorState.jsx";
import { SkeletonBlock } from "../components/ui/Skeleton.jsx";
import { RECRUITER_NAV } from "../constants";
import { useAuth } from "../context/AuthContext.jsx";
import {
  getFunnel,
  getSkillDistribution,
  getDepartmentBreakdown,
} from "../services/recruiterAnalyticsService";

const FUNNEL_STAGES = [
  "APPLIED",
  "SHORTLISTED",
  "ASSESSMENT",
  "TECHNICAL_INTERVIEW",
  "HR_INTERVIEW",
  "OFFERED",
  "HIRED",
];

function humanize(status) {
  return status
    .split("_")
    .map((word) => word[0] + word.slice(1).toLowerCase())
    .join(" ");
}

export default function RecruiterAnalyticsPage() {
  const { user } = useAuth();

  const [funnel, setFunnel] = useState([]);
  const [rejectedCount, setRejectedCount] = useState(0);
  const [skills, setSkills] = useState([]);
  const [departments, setDepartments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const load = useCallback(async () => {
    setLoading(true);
    setError("");
    try {
      const [funnelData, skillData, departmentData] = await Promise.all([
        getFunnel(),
        getSkillDistribution(),
        getDepartmentBreakdown(),
      ]);

      setRejectedCount(funnelData.find((entry) => entry.stage === "REJECTED")?.count || 0);
      setFunnel(
        FUNNEL_STAGES.map((stage) => ({
          name: humanize(stage),
          value: funnelData.find((entry) => entry.stage === stage)?.count || 0,
        }))
      );
      setSkills(skillData);
      setDepartments(departmentData);
    } catch (err) {
      setError(err.friendlyMessage || "Failed to load analytics");
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    load();
  }, [load]);

  const maxDeptCount = Math.max(1, ...departments.map((entry) => entry.applicantCount));

  return (
    <DashboardLayout
      navItems={RECRUITER_NAV}
      roleLabel="Recruiter"
      title="Analytics"
      userName={user?.name || "User"}
    >
      {loading && (
        <div className="grid gap-5 lg:grid-cols-2">
          <SkeletonBlock className="h-96" />
          <SkeletonBlock className="h-96" />
        </div>
      )}

      {!loading && error && (
        <div className="glass-card">
          <ErrorState message={error} onRetry={load} />
        </div>
      )}

      {!loading && !error && (
        <div className="grid gap-5 lg:grid-cols-2">
          <motion.div initial={{ opacity: 0, y: 16 }} animate={{ opacity: 1, y: 0 }} className="glass-card p-6">
            <div className="flex items-center justify-between">
              <div className="flex items-center gap-2">
                <FiBarChart2 className="text-primary-400" size={16} />
                <h2 className="font-display text-lg font-semibold text-white">Hiring funnel</h2>
              </div>
              <span className="rounded-full bg-rose-500/10 px-2.5 py-1 text-xs font-semibold text-rose-400">
                {rejectedCount} rejected
              </span>
            </div>
            <FunnelChart data={funnel} />
          </motion.div>

          <motion.div
            initial={{ opacity: 0, y: 16 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.1 }}
            className="glass-card p-6"
          >
            <h2 className="font-display text-lg font-semibold text-white">Skill distribution</h2>
            <p className="mt-1 text-xs text-slate-500">Applicants vs. jobs requiring each skill</p>
            <SkillDistributionChart data={skills} />
          </motion.div>

          <motion.div
            initial={{ opacity: 0, y: 16 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.2 }}
            className="glass-card p-6 lg:col-span-2"
          >
            <h2 className="font-display text-lg font-semibold text-white">Applicants by department</h2>
            <div className="mt-5 space-y-4">
              {departments.length === 0 && <p className="text-sm text-slate-500">No applicants yet.</p>}
              {departments.map((entry) => (
                <div key={entry.branch}>
                  <div className="flex items-center justify-between text-sm">
                    <span className="text-slate-300">{entry.branch}</span>
                    <span className="font-semibold text-white">{entry.applicantCount}</span>
                  </div>
                  <div className="mt-2 h-2 overflow-hidden rounded-full bg-white/5">
                    <div
                      className="h-full rounded-full bg-brand-gradient"
                      style={{ width: (entry.applicantCount / maxDeptCount) * 100 + "%" }}
                    />
                  </div>
                </div>
              ))}
            </div>
          </motion.div>
        </div>
      )}
    </DashboardLayout>
  );
}
