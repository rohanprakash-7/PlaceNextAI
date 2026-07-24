import { FiMenu, FiSearch } from "react-icons/fi";
import ThemeToggle from "../ui/ThemeToggle.jsx";
import NotificationBell from "../notifications/NotificationBell.jsx";

export default function DashboardNavbar({ title, userName, onMenuClick }) {
  const initials = userName
    .split(" ")
    .map((part) => part[0])
    .join("")
    .slice(0, 2)
    .toUpperCase();

  return (
    <header className="glass-strong sticky top-0 z-30 flex h-16 items-center gap-4 border-b border-slate-200 px-4 dark:border-white/5 sm:px-6">
      <button
        type="button"
        aria-label="Open sidebar"
        onClick={onMenuClick}
        className="glass flex h-9 w-9 shrink-0 items-center justify-center rounded-lg text-slate-600 dark:text-slate-300 lg:hidden"
      >
        <FiMenu size={17} />
      </button>

      <h1 className="font-display text-base font-semibold text-slate-900 dark:text-white sm:text-lg">{title}</h1>

      <div className="ml-auto flex items-center gap-3">
        <div className="relative hidden sm:block">
          <FiSearch className="absolute left-3.5 top-1/2 -translate-y-1/2 text-slate-500" size={15} />
          <input
            type="search"
            placeholder="Search…"
            className="glass w-52 rounded-xl py-2 pl-9 pr-3 text-sm text-slate-700 placeholder-slate-400 outline-none transition-colors focus:border-primary-500/50 dark:text-slate-200 dark:placeholder-slate-500 md:w-64"
          />
        </div>

        <ThemeToggle />

        <NotificationBell />

        <div className="flex items-center gap-2.5">
          <span className="flex h-9 w-9 items-center justify-center rounded-xl bg-brand-gradient text-xs font-bold text-white shadow-glow-sm">
            {initials}
          </span>
          <span className="hidden text-sm font-medium text-slate-700 dark:text-slate-200 md:block">{userName}</span>
        </div>
      </div>
    </header>
  );
}
