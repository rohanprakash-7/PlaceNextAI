import { FiMenu, FiSearch, FiBell } from "react-icons/fi";

export default function DashboardNavbar({ title, userName, onMenuClick }) {
  const initials = userName
    .split(" ")
    .map((part) => part[0])
    .join("")
    .slice(0, 2)
    .toUpperCase();

  return (
    <header className="glass-strong sticky top-0 z-30 flex h-16 items-center gap-4 border-b border-white/5 px-4 sm:px-6">
      <button
        type="button"
        aria-label="Open sidebar"
        onClick={onMenuClick}
        className="glass flex h-9 w-9 shrink-0 items-center justify-center rounded-lg text-slate-300 lg:hidden"
      >
        <FiMenu size={17} />
      </button>

      <h1 className="font-display text-base font-semibold text-white sm:text-lg">{title}</h1>

      <div className="ml-auto flex items-center gap-3">
        <div className="relative hidden sm:block">
          <FiSearch className="absolute left-3.5 top-1/2 -translate-y-1/2 text-slate-500" size={15} />
          <input
            type="search"
            placeholder="Search…"
            className="glass w-52 rounded-xl py-2 pl-9 pr-3 text-sm text-slate-200 placeholder-slate-500 outline-none transition-colors focus:border-primary-500/50 md:w-64"
          />
        </div>

        <button
          type="button"
          aria-label="Notifications"
          className="glass relative flex h-9 w-9 items-center justify-center rounded-xl text-slate-300 transition-colors hover:text-white"
        >
          <FiBell size={16} />
          <span className="absolute right-2 top-2 h-1.5 w-1.5 rounded-full bg-primary-400" />
        </button>

        <div className="flex items-center gap-2.5">
          <span className="flex h-9 w-9 items-center justify-center rounded-xl bg-brand-gradient text-xs font-bold text-white shadow-glow-sm">
            {initials}
          </span>
          <span className="hidden text-sm font-medium text-slate-200 md:block">{userName}</span>
        </div>
      </div>
    </header>
  );
}
