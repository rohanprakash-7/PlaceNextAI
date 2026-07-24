import { motion } from "framer-motion";

export default function SectionHeading({ eyebrow, title, highlight, description }) {
  return (
    <motion.div
      initial={{ opacity: 0, y: 24 }}
      whileInView={{ opacity: 1, y: 0 }}
      viewport={{ once: true, margin: "-80px" }}
      transition={{ duration: 0.6, ease: [0.21, 0.47, 0.32, 0.98] }}
      className="mx-auto mb-14 max-w-2xl text-center"
    >
      {eyebrow && (
        <span className="glass mb-4 inline-flex items-center rounded-full px-4 py-1.5 text-xs font-semibold uppercase tracking-widest text-primary-400">
          {eyebrow}
        </span>
      )}
      <h2 className="font-display text-3xl font-semibold tracking-tight text-slate-900 dark:text-white sm:text-4xl">
        {title} {highlight && <span className="text-gradient">{highlight}</span>}
      </h2>
      {description && <p className="mt-4 text-base leading-relaxed text-slate-500 dark:text-slate-400">{description}</p>}
    </motion.div>
  );
}
