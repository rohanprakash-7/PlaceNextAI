import { motion, AnimatePresence } from "framer-motion";
import { FiLogOut, FiX } from "react-icons/fi";
import { Link, useLocation } from "react-router-dom";
import Logo from "../ui/Logo.jsx";
import { useAuth } from "../../context/AuthContext.jsx";

function SidebarContent({ navItems, roleLabel, onNavigate }) {
  const { logout } = useAuth();
  const { pathname } = useLocation();

  return (
    <div className="flex h-full flex-col">
      <div className="flex h-16 items-center justify-between border-b border-white/5 px-5">
        <Logo size="sm" />
        {onNavigate && (
          <button
            type="button"
            aria-label="Close sidebar"
            onClick={onNavigate}
            className="glass flex h-8 w-8 items-center justify-center rounded-lg text-slate-300 lg:hidden"
          >
            <FiX size={15} />
          </button>
        )}
      </div>

      <div className="px-5 pt-5">
        <span className="glass inline-flex rounded-full px-3 py-1 text-[11px] font-semibold uppercase tracking-widest text-primary-400">
          {roleLabel}
        </span>
      </div>

      <nav className="mt-4 flex-1 space-y-1 overflow-y-auto px-3 pb-4">
        {navItems.map((navItem) => {
          const Icon = navItem.icon;
          const active = navItem.to ? pathname === navItem.to : false;
          const itemClasses =
            "group flex w-full items-center gap-3 rounded-xl px-3 py-2.5 text-sm font-medium transition-all duration-200 " +
            (active
              ? "bg-white/[0.06] text-white shadow-glow-sm"
              : "text-slate-400 hover:bg-white/[0.04] hover:text-white");
          const iconClasses = active
            ? "text-primary-400"
            : "text-slate-500 transition-colors group-hover:text-primary-400";

          if (navItem.to) {
            return (
              <Link key={navItem.label} to={navItem.to} onClick={onNavigate} className={itemClasses}>
                <Icon size={17} className={iconClasses} />
                {navItem.label}
                {active && <span className="ml-auto h-1.5 w-1.5 rounded-full bg-primary-400" />}
              </Link>
            );
          }

          return (
            <button key={navItem.label} type="button" onClick={onNavigate} className={itemClasses}>
              <Icon size={17} className={iconClasses} />
              {navItem.label}
            </button>
          );
        })}
      </nav>

      <div className="border-t border-white/5 p-3">
        <button
          type="button"
          onClick={() => logout()}
          className="flex w-full items-center gap-3 rounded-xl px-3 py-2.5 text-sm font-medium text-slate-400 transition-colors hover:bg-rose-500/10 hover:text-rose-400"
        >
          <FiLogOut size={17} />
          Log out
        </button>
      </div>
    </div>
  );
}

export default function Sidebar({ navItems, roleLabel, mobileOpen, onClose }) {
  return (
    <>
      <aside className="glass-strong fixed inset-y-0 left-0 z-40 hidden w-64 border-r border-white/5 lg:block">
        <SidebarContent navItems={navItems} roleLabel={roleLabel} />
      </aside>

      <AnimatePresence>
        {mobileOpen && (
          <>
            <motion.div
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              exit={{ opacity: 0 }}
              transition={{ duration: 0.2 }}
              onClick={onClose}
              className="fixed inset-0 z-40 bg-black/60 backdrop-blur-sm lg:hidden"
            />
            <motion.aside
              initial={{ x: "-100%" }}
              animate={{ x: 0 }}
              exit={{ x: "-100%" }}
              transition={{ type: "spring", stiffness: 320, damping: 32 }}
              className="glass-strong fixed inset-y-0 left-0 z-50 w-72 border-r border-white/5 lg:hidden"
            >
              <SidebarContent navItems={navItems} roleLabel={roleLabel} onNavigate={onClose} />
            </motion.aside>
          </>
        )}
      </AnimatePresence>
    </>
  );
}
