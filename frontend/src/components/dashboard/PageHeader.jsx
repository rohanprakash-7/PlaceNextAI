import { motion } from "framer-motion";
import Breadcrumb from "./Breadcrumb.jsx";

export default function PageHeader({ breadcrumbs, title, description, icon: Icon }) {
  return (
    <motion.div
      initial={{ opacity: 0, y: 14 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.45, ease: [0.21, 0.47, 0.32, 0.98] }}
      className="mb-6"
    >
      <Breadcrumb items={breadcrumbs} />
      <div className="mt-3 flex items-center gap-3">
        {Icon && (
          <span className="flex h-11 w-11 items-center justify-center rounded-xl bg-brand-gradient text-white shadow-glow-sm">
            <Icon size={19} />
          </span>
        )}
        <div>
          <h1 className="font-display text-2xl font-semibold tracking-tight text-white">{title}</h1>
          <p className="mt-1 text-sm text-slate-400">{description}</p>
        </div>
      </div>
    </motion.div>
  );
}
