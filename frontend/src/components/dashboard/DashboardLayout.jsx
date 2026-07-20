import { useState } from "react";
import { motion } from "framer-motion";
import Sidebar from "./Sidebar.jsx";
import DashboardNavbar from "./DashboardNavbar.jsx";

export default function DashboardLayout({ navItems, roleLabel, title, userName, children }) {
  const [mobileOpen, setMobileOpen] = useState(false);

  return (
    <div className="min-h-screen bg-night-950 bg-page-glow">
      <Sidebar
        navItems={navItems}
        roleLabel={roleLabel}
        mobileOpen={mobileOpen}
        onClose={() => setMobileOpen(false)}
      />

      <div className="lg:pl-64">
        <DashboardNavbar
          title={title}
          userName={userName}
          onMenuClick={() => setMobileOpen(true)}
        />

        <motion.main
          initial={{ opacity: 0, y: 16 }}
          animate={{ opacity: 1, y: 0 }}
          exit={{ opacity: 0, y: -12 }}
          transition={{ duration: 0.45, ease: [0.21, 0.47, 0.32, 0.98] }}
          className="mx-auto max-w-7xl p-4 sm:p-6 lg:p-8"
        >
          {children}
        </motion.main>
      </div>
    </div>
  );
}
