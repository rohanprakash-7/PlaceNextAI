import { motion } from "framer-motion";
import DashboardLayout from "./DashboardLayout.jsx";
import PageHeader from "./PageHeader.jsx";
import EmptyState from "../ui/EmptyState.jsx";
import { ADMIN_NAV } from "../../constants";
import { useAuth } from "../../context/AuthContext.jsx";

export default function AdminPage({
  navTitle,
  title,
  description,
  icon,
  emptyIcon,
  emptyTitle,
  emptyMessage,
  highlights = [],
}) {
  const { user } = useAuth();

  return (
    <DashboardLayout
      navItems={ADMIN_NAV}
      roleLabel="Admin"
      title={navTitle}
      userName={user?.name || "Admin"}
    >
      <PageHeader
        breadcrumbs={[
          { label: "Admin", to: "/dashboard/admin" },
          { label: title },
        ]}
        title={title}
        description={description}
        icon={icon}
      />

      {highlights.length > 0 && (
        <div className="mb-5 grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
          {highlights.map((highlight, index) => {
            const Icon = highlight.icon;
            return (
              <motion.div
                key={highlight.label}
                initial={{ opacity: 0, y: 16 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ duration: 0.45, delay: 0.1 + index * 0.07 }}
                className="glass-card p-5"
              >
                <span className="flex h-10 w-10 items-center justify-center rounded-xl bg-white/[0.06] text-primary-400">
                  <Icon size={17} />
                </span>
                <p className="mt-4 font-display text-xl font-semibold text-white">{highlight.value}</p>
                <p className="mt-1 text-sm text-slate-400">{highlight.label}</p>
              </motion.div>
            );
          })}
        </div>
      )}

      <motion.div
        initial={{ opacity: 0, y: 18 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.5, delay: 0.15 }}
        className="glass-card"
      >
        <EmptyState icon={emptyIcon} title={emptyTitle} message={emptyMessage} />
      </motion.div>
    </DashboardLayout>
  );
}
