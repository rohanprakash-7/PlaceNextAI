import { motion } from "framer-motion";
import { STATS } from "../../constants";

export default function Stats() {
  return (
    <section id="stats" className="py-20">
      <div className="mx-auto max-w-7xl px-5 sm:px-8">
        <div className="glass-card grid grid-cols-2 gap-y-10 px-6 py-12 lg:grid-cols-4">
          {STATS.map((stat, index) => (
            <motion.div
              key={stat.label}
              initial={{ opacity: 0, y: 18 }}
              whileInView={{ opacity: 1, y: 0 }}
              viewport={{ once: true }}
              transition={{ duration: 0.55, delay: index * 0.1 }}
              className="text-center"
            >
              <p className="font-display text-4xl font-semibold text-gradient">{stat.value}</p>
              <p className="mt-2 text-sm text-slate-400">{stat.label}</p>
            </motion.div>
          ))}
        </div>
      </div>
    </section>
  );
}
