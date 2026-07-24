import { Link } from "react-router-dom";
import { FiChevronRight } from "react-icons/fi";

export default function Breadcrumb({ items }) {
  return (
    <nav aria-label="Breadcrumb" className="flex items-center gap-1.5 text-xs text-slate-500">
      {items.map((item, index) => {
        const isLast = index === items.length - 1;
        return (
          <span key={item.label} className="flex items-center gap-1.5">
            {item.to && !isLast ? (
              <Link to={item.to} className="transition-colors hover:text-slate-900 dark:hover:text-slate-300">
                {item.label}
              </Link>
            ) : (
              <span className={isLast ? "font-medium text-slate-900 dark:text-slate-300" : ""}>{item.label}</span>
            )}
            {!isLast && <FiChevronRight size={12} className="text-slate-400 dark:text-slate-600" />}
          </span>
        );
      })}
    </nav>
  );
}
