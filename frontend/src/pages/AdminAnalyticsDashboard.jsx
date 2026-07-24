import { useCallback, useEffect, useState } from "react";
import { motion } from "framer-motion";
import {
  FiUsers,
  FiCheckCircle,
  FiTrendingUp,
  FiAlertTriangle,
  FiDownload,
  FiFileText,
  FiMic,
  FiCpu,
  FiActivity,
} from "react-icons/fi";
import DashboardLayout from "../components/dashboard/DashboardLayout.jsx";
import StatCard from "../components/ui/StatCard.jsx";
import RiskDistributionChart from "../components/analytics/RiskDistributionChart.jsx";
import HiringTrendsChart from "../components/analytics/HiringTrendsChart.jsx";
import SkillsBarChart from "../components/analytics/SkillsBarChart.jsx";
import WeeklyActivityHeatmap from "../components/visualizations/WeeklyActivityHeatmap.jsx";
import ErrorState from "../components/ui/ErrorState.jsx";
import { SkeletonBlock } from "../components/ui/Skeleton.jsx";
import { ADMIN_NAV } from "../constants";
import { useAuth } from "../context/AuthContext.jsx";
import {
  getOverview,
  getDepartments,
  getRecruiterActivity,
  getRiskDistribution,
  getColleges,
  getStudentAnalytics,
  getHiringTrends,
  getResumeStats,
  getInterviewStats,
  getSkillAnalytics,
  getAiPredictionAnalytics,
  getPlatformHeatmap,
  downloadReport,
} from "../services/adminAnalyticsService";

export default function AdminAnalyticsDashboard() {
  const { user } = useAuth();

  const [overview, setOverview] = useState(null);
  const [departments, setDepartments] = useState([]);
  const [recruiters, setRecruiters] = useState([]);
  const [risk, setRisk] = useState(null);
  const [colleges, setColleges] = useState([]);
  const [studentStats, setStudentStats] = useState(null);
  const [hiringTrends, setHiringTrends] = useState([]);
  const [resumeStats, setResumeStats] = useState(null);
  const [interviewStats, setInterviewStats] = useState(null);
  const [skills, setSkills] = useState(null);
  const [aiPredictions, setAiPredictions] = useState(null);
  const [heatmap, setHeatmap] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [exporting, setExporting] = useState("");

  const handleExport = async (format) => {
    setExporting(format);
    try {
      await downloadReport(format);
    } catch (err) {
      setError(err.friendlyMessage || "Export failed");
    } finally {
      setExporting("");
    }
  };

  const load = useCallback(async () => {
    setLoading(true);
    setError("");
    try {
      const [
        overviewData,
        departmentData,
        recruiterData,
        riskData,
        collegeData,
        studentStatsData,
        hiringTrendsData,
        resumeStatsData,
        interviewStatsData,
        skillsData,
        aiPredictionsData,
        heatmapData,
      ] = await Promise.all([
        getOverview(),
        getDepartments(),
        getRecruiterActivity(),
        getRiskDistribution(),
        getColleges(),
        getStudentAnalytics(),
        getHiringTrends(),
        getResumeStats(),
        getInterviewStats(),
        getSkillAnalytics(),
        getAiPredictionAnalytics(),
        getPlatformHeatmap(90),
      ]);
      setOverview(overviewData);
      setDepartments(departmentData);
      setRecruiters(recruiterData);
      setRisk(riskData);
      setColleges(collegeData);
      setStudentStats(studentStatsData);
      setHiringTrends(hiringTrendsData);
      setResumeStats(resumeStatsData);
      setInterviewStats(interviewStatsData);
      setSkills(skillsData);
      setAiPredictions(aiPredictionsData);
      setHeatmap(heatmapData);
    } catch (err) {
      setError(err.friendlyMessage || "Failed to load platform analytics");
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    load();
  }, [load]);

  const maxDeptCount = Math.max(1, ...departments.map((entry) => entry.studentCount));

  const stats = overview
    ? [
        { icon: FiUsers, label: "Total students", value: overview.totalStudents },
        { icon: FiCheckCircle, label: "Placed", value: overview.placedStudents, trend: overview.placementPercent + "%", trendUp: true },
        { icon: FiTrendingUp, label: "Avg. readiness score", value: overview.averageReadiness },
        { icon: FiAlertTriangle, label: "High risk students", value: risk?.high ?? 0, trendUp: false },
      ]
    : [];

  return (
    <DashboardLayout navItems={ADMIN_NAV} roleLabel="Admin" title="Platform Analytics" userName={user?.name || "User"}>
      {loading && (
        <div className="grid gap-5 lg:grid-cols-2">
          <SkeletonBlock className="h-32 lg:col-span-2" />
          <SkeletonBlock className="h-80" />
          <SkeletonBlock className="h-80" />
        </div>
      )}

      {!loading && error && (
        <div className="glass-card">
          <ErrorState message={error} onRetry={load} />
        </div>
      )}

      {!loading && !error && (
        <>
          <div className="mb-4 flex justify-end gap-2">
            <button
              type="button"
              onClick={() => handleExport("pdf")}
              disabled={Boolean(exporting)}
              className="glass inline-flex items-center gap-2 rounded-xl px-3.5 py-2 text-xs font-medium text-slate-700 dark:text-slate-300 transition-colors hover:text-slate-900 dark:hover:text-white disabled:opacity-50"
            >
              <FiDownload size={13} /> {exporting === "pdf" ? "Exporting…" : "Export PDF"}
            </button>
            <button
              type="button"
              onClick={() => handleExport("xlsx")}
              disabled={Boolean(exporting)}
              className="glass inline-flex items-center gap-2 rounded-xl px-3.5 py-2 text-xs font-medium text-slate-700 dark:text-slate-300 transition-colors hover:text-slate-900 dark:hover:text-white disabled:opacity-50"
            >
              <FiDownload size={13} /> {exporting === "xlsx" ? "Exporting…" : "Export Excel"}
            </button>
          </div>

          <div className="grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
            {stats.map((stat, index) => (
              <StatCard key={stat.label} {...stat} delay={index * 0.07} />
            ))}
          </div>

          <div className="mt-6 grid gap-5 lg:grid-cols-2">
            <motion.div initial={{ opacity: 0, y: 16 }} animate={{ opacity: 1, y: 0 }} className="glass-card p-6">
              <h2 className="font-display text-lg font-semibold text-slate-900 dark:text-white">Placement risk distribution</h2>
              <p className="mt-1 text-xs text-slate-500">From the explainable placement-probability engine</p>
              <RiskDistributionChart distribution={risk} />
            </motion.div>

            <motion.div
              initial={{ opacity: 0, y: 16 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: 0.1 }}
              className="glass-card p-6"
            >
              <h2 className="font-display text-lg font-semibold text-slate-900 dark:text-white">Department-wise readiness</h2>
              <div className="mt-5 space-y-4">
                {departments.length === 0 && <p className="text-sm text-slate-500">No students yet.</p>}
                {departments.map((entry) => (
                  <div key={entry.branch}>
                    <div className="flex items-center justify-between text-sm">
                      <span className="text-slate-700 dark:text-slate-300">
                        {entry.branch} <span className="text-slate-500">({entry.studentCount})</span>
                      </span>
                      <span className="font-semibold text-slate-900 dark:text-white">{entry.averageReadiness}</span>
                    </div>
                    <div className="mt-2 h-2 overflow-hidden rounded-full bg-white/5">
                      <div
                        className="h-full rounded-full bg-brand-gradient"
                        style={{ width: (entry.studentCount / maxDeptCount) * 100 + "%" }}
                      />
                    </div>
                  </div>
                ))}
              </div>
            </motion.div>

            <motion.div
              initial={{ opacity: 0, y: 16 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: 0.2 }}
              className="glass-card overflow-hidden lg:col-span-2"
            >
              <div className="border-b border-slate-200 dark:border-white/5 px-6 py-5">
                <h2 className="font-display text-lg font-semibold text-slate-900 dark:text-white">Recruiter activity</h2>
              </div>
              <div className="overflow-x-auto">
                <table className="w-full min-w-[560px] text-left text-sm">
                  <thead>
                    <tr className="border-b border-slate-200 dark:border-white/5 text-xs uppercase tracking-wider text-slate-500">
                      <th className="px-6 py-3.5 font-medium">Company</th>
                      <th className="px-6 py-3.5 font-medium">Recruiter</th>
                      <th className="px-6 py-3.5 font-medium">Applications received</th>
                      <th className="px-6 py-3.5 font-medium">Feedback submitted</th>
                    </tr>
                  </thead>
                  <tbody>
                    {recruiters.map((entry) => (
                      <tr key={entry.recruiterId} className="border-b border-slate-200 dark:border-white/5 last:border-0">
                        <td className="px-6 py-3.5 font-medium text-slate-900 dark:text-white">{entry.companyName}</td>
                        <td className="px-6 py-3.5 text-slate-500 dark:text-slate-400">{entry.recruiterName}</td>
                        <td className="px-6 py-3.5 text-slate-700 dark:text-slate-300">{entry.applicationsReceived}</td>
                        <td className="px-6 py-3.5 text-slate-700 dark:text-slate-300">{entry.feedbackCount}</td>
                      </tr>
                    ))}
                    {recruiters.length === 0 && (
                      <tr>
                        <td colSpan={4} className="px-6 py-6 text-center text-slate-500">
                          No recruiters yet.
                        </td>
                      </tr>
                    )}
                  </tbody>
                </table>
              </div>
            </motion.div>
          </div>

          {studentStats && (
            <div className="mt-5 grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
              <StatCard icon={FiFileText} label="Avg. resume score" value={studentStats.averageResumeScore} />
              <StatCard icon={FiMic} label="Avg. mock interview score" value={studentStats.averageMockInterviewScore} />
              <StatCard icon={FiTrendingUp} label="Avg. CGPA" value={studentStats.averageCgpa} />
              <StatCard icon={FiCpu} label="AI predictions computed" value={aiPredictions?.totalPredictions ?? 0} />
            </div>
          )}

          <div className="mt-5 grid gap-5 lg:grid-cols-2">
            <motion.div initial={{ opacity: 0, y: 16 }} animate={{ opacity: 1, y: 0 }} className="glass-card p-6">
              <h2 className="font-display text-lg font-semibold text-slate-900 dark:text-white">Hiring trends</h2>
              <p className="mt-1 text-xs text-slate-500">Applications received vs. hires, last 6 months</p>
              <HiringTrendsChart trends={hiringTrends} />
            </motion.div>

            <motion.div
              initial={{ opacity: 0, y: 16 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: 0.1 }}
              className="glass-card overflow-hidden"
            >
              <div className="border-b border-slate-200 dark:border-white/5 px-6 py-5">
                <h2 className="font-display text-lg font-semibold text-slate-900 dark:text-white">College-wise placement</h2>
              </div>
              <div className="max-h-[300px] overflow-y-auto">
                <table className="w-full min-w-[420px] text-left text-sm">
                  <thead>
                    <tr className="border-b border-slate-200 dark:border-white/5 text-xs uppercase tracking-wider text-slate-500">
                      <th className="px-6 py-3 font-medium">College</th>
                      <th className="px-6 py-3 font-medium">Students</th>
                      <th className="px-6 py-3 font-medium">Placement %</th>
                    </tr>
                  </thead>
                  <tbody>
                    {colleges.map((entry) => (
                      <tr key={entry.college} className="border-b border-slate-200 dark:border-white/5 last:border-0">
                        <td className="px-6 py-3 font-medium text-slate-900 dark:text-white">{entry.college}</td>
                        <td className="px-6 py-3 text-slate-700 dark:text-slate-300">{entry.studentCount}</td>
                        <td className="px-6 py-3 text-slate-700 dark:text-slate-300">{entry.placementPercent}%</td>
                      </tr>
                    ))}
                    {colleges.length === 0 && (
                      <tr>
                        <td colSpan={3} className="px-6 py-6 text-center text-slate-500">
                          No students yet.
                        </td>
                      </tr>
                    )}
                  </tbody>
                </table>
              </div>
            </motion.div>
          </div>

          <div className="mt-5 grid gap-5 lg:grid-cols-2">
            <motion.div initial={{ opacity: 0, y: 16 }} animate={{ opacity: 1, y: 0 }} className="glass-card p-6">
              <h2 className="font-display text-lg font-semibold text-slate-900 dark:text-white">Top student skills</h2>
              <p className="mt-1 text-xs text-slate-500">Most common skills across student profiles</p>
              <SkillsBarChart data={skills?.topStudentSkills || []} color="#8b5cf6" />
            </motion.div>

            <motion.div
              initial={{ opacity: 0, y: 16 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: 0.1 }}
              className="glass-card p-6"
            >
              <h2 className="font-display text-lg font-semibold text-slate-900 dark:text-white">Most in-demand skills</h2>
              <p className="mt-1 text-xs text-slate-500">Most requested skills across active job postings</p>
              <SkillsBarChart data={skills?.topDemandedSkills || []} color="#3b82f6" />
            </motion.div>
          </div>

          <div className="mt-5 grid gap-5 lg:grid-cols-2">
            <motion.div initial={{ opacity: 0, y: 16 }} animate={{ opacity: 1, y: 0 }} className="glass-card p-6">
              <div className="flex items-center gap-2">
                <FiActivity className="text-primary-400" size={16} />
                <h2 className="font-display text-lg font-semibold text-slate-900 dark:text-white">Platform activity, last 90 days</h2>
              </div>
              <p className="mt-1 text-xs text-slate-500">Every recorded student action across the platform</p>
              <div className="mt-4">
                <WeeklyActivityHeatmap counts={heatmap} days={90} />
              </div>
            </motion.div>

            <motion.div
              initial={{ opacity: 0, y: 16 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: 0.1 }}
              className="glass-card p-6"
            >
              <h2 className="font-display text-lg font-semibold text-slate-900 dark:text-white">Resume &amp; interview stats</h2>
              <div className="mt-4 space-y-3 text-sm">
                <div className="flex items-center justify-between">
                  <span className="text-slate-500 dark:text-slate-400">Resume versions uploaded</span>
                  <span className="font-semibold text-slate-900 dark:text-white">{resumeStats?.totalResumeVersions ?? 0}</span>
                </div>
                <div className="flex items-center justify-between">
                  <span className="text-slate-500 dark:text-slate-400">Students with a resume</span>
                  <span className="font-semibold text-slate-900 dark:text-white">{resumeStats?.studentsWithResume ?? 0}</span>
                </div>
                <div className="flex items-center justify-between">
                  <span className="text-slate-500 dark:text-slate-400">Avg. ATS score</span>
                  <span className="font-semibold text-slate-900 dark:text-white">{resumeStats?.averageAtsScore ?? 0}</span>
                </div>
                <div className="my-2 border-t border-slate-200 dark:border-white/5" />
                <div className="flex items-center justify-between">
                  <span className="text-slate-500 dark:text-slate-400">Mock interviews completed</span>
                  <span className="font-semibold text-slate-900 dark:text-white">{interviewStats?.totalMockInterviews ?? 0}</span>
                </div>
                <div className="flex items-center justify-between">
                  <span className="text-slate-500 dark:text-slate-400">Success stories shared</span>
                  <span className="font-semibold text-slate-900 dark:text-white">{interviewStats?.totalSuccessStories ?? 0}</span>
                </div>
                <div className="flex items-center justify-between">
                  <span className="text-slate-500 dark:text-slate-400">Avg. placement probability (AI)</span>
                  <span className="font-semibold text-slate-900 dark:text-white">{aiPredictions?.averageProbability ?? 0}%</span>
                </div>
              </div>
            </motion.div>
          </div>
        </>
      )}
    </DashboardLayout>
  );
}
