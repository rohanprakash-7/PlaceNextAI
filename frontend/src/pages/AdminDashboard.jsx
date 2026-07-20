import { motion } from "framer-motion";
import { FiUsers, FiBriefcase, FiCpu, FiActivity } from "react-icons/fi";
import DashboardLayout from "../components/dashboard/DashboardLayout.jsx";
import StatCard from "../components/ui/StatCard.jsx";
import { ADMIN_NAV } from "../constants";
import { useAuth } from "../context/AuthContext.jsx";

const STATS = [
  { icon: FiUsers, label: "Registered students", value: "4,382", trend: "+9%", trendUp: true },
  { icon: FiBriefcase, label: "Recruiter accounts", value: "156", trend: "+12", trendUp: true },
  { icon: FiCpu, label: "AI requests today", value: "18.4k", trend: "+22%", trendUp: true },
  { icon: FiActivity, label: "System uptime", value: "99.98%", trend: "30d", trendUp: true },
];

const SERVICES = [
  { name: "Spring Boot Core API", status: "Operational", latency: "42 ms" },
  { name: "FastAPI AI Service", status: "Operational", latency: "310 ms" },
  { name: "MySQL Database", status: "Operational", latency: "8 ms" },
  { name: "Resume Parser Agent", status: "Degraded", latency: "1.2 s" },
];

const ACTIVITY = [
  { text: "New recruiter account approved — TechNova Pvt Ltd", time: "6 min ago" },
  { text: "AI shortlist generated for Backend Engineer Intern (214 applicants)", time: "18 min ago" },
  { text: "Batch readiness report exported by placement cell", time: "1 hr ago" },
  { text: "Model updated — sentence-transformer v3 deployed", time: "3 hrs ago" },
];

export default function AdminDashboard() {
  const { user } = useAuth();

  return (
    <DashboardLayout
      navItems={ADMIN_NAV}
      roleLabel="Admin"
      title="Admin Overview"
      userName={user?.name || "User"}
    >
      <div className="grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
        {STATS.map((stat, index) => (
          <StatCard key={stat.label} {...stat} delay={index * 0.07} />
        ))}
      </div>

      <div className="mt-6 grid gap-5 lg:grid-cols-2">
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.5, delay: 0.25 }}
          className="glass-card p-6"
        >
          <h2 className="font-display text-lg font-semibold text-white">Service health</h2>
          <div className="mt-5 space-y-3">
            {SERVICES.map((service) => {
              const healthy = service.status === "Operational";
              return (
                <div
                  key={service.name}
                  className="flex items-center justify-between rounded-xl border border-white/5 bg-white/[0.03] px-4 py-3.5"
                >
                  <div className="flex items-center gap-3">
                    <span
                      className={
                        "h-2 w-2 rounded-full " +
                        (healthy ? "bg-emerald-400 shadow-[0_0_8px_rgba(52,211,153,0.7)]" : "bg-amber-400 shadow-[0_0_8px_rgba(251,191,36,0.7)]")
                      }
                    />
                    <span className="text-sm font-medium text-white">{service.name}</span>
                  </div>
                  <div className="flex items-center gap-4">
                    <span className="text-xs text-slate-500">{service.latency}</span>
                    <span
                      className={
                        "rounded-full px-2.5 py-1 text-xs font-semibold " +
                        (healthy ? "bg-emerald-500/10 text-emerald-400" : "bg-amber-500/10 text-amber-400")
                      }
                    >
                      {service.status}
                    </span>
                  </div>
                </div>
              );
            })}
          </div>
        </motion.div>

        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.5, delay: 0.35 }}
          className="glass-card p-6"
        >
          <h2 className="font-display text-lg font-semibold text-white">Recent activity</h2>
          <div className="mt-5 space-y-1">
            {ACTIVITY.map((entry, index) => (
              <motion.div
                key={entry.text}
                initial={{ opacity: 0, x: -10 }}
                animate={{ opacity: 1, x: 0 }}
                transition={{ duration: 0.4, delay: 0.45 + index * 0.09 }}
                className="flex gap-3 rounded-xl px-3 py-3 transition-colors hover:bg-white/[0.03]"
              >
                <span className="mt-1.5 h-1.5 w-1.5 shrink-0 rounded-full bg-primary-400" />
                <div>
                  <p className="text-sm text-slate-300">{entry.text}</p>
                  <p className="mt-0.5 text-xs text-slate-500">{entry.time}</p>
                </div>
              </motion.div>
            ))}
          </div>
        </motion.div>
      </div>
    </DashboardLayout>
  );
}
