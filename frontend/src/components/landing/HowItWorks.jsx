import { motion } from "framer-motion";
import SectionHeading from "../ui/SectionHeading.jsx";
import { HOW_IT_WORKS } from "../../constants";

export default function HowItWorks() {
  return (
    <section id="how-it-works" className="relative py-24">
      <div className="pointer-events-none absolute left-1/2 top-0 h-72 w-[36rem] -translate-x-1/2 rounded-full bg-primary-600/10 blur-3xl" />

      <div className="relative mx-auto max-w-7xl px-5 sm:px-8">
        <SectionHeading
          eyebrow="How it works"
          title="From profile to offer in"
          highlight="three steps"
        />

        <div className="grid gap-5 md:grid-cols-3">
          {HOW_IT_WORKS.map((step, index) => (
            <motion.div
              key={step.step}
              initial={{ opacity: 0, y: 28 }}
              whileInView={{ opacity: 1, y: 0 }}
              viewport={{ once: true, margin: "-60px" }}
              transition={{ duration: 0.6, delay: index * 0.12, ease: [0.21, 0.47, 0.32, 0.98] }}
              className="glass-card relative overflow-hidden p-7"
            >
              <span className="font-display text-5xl font-semibold text-white/[0.07]">{step.step}</span>
              <h3 className="mt-4 font-display text-lg font-semibold text-slate-900 dark:text-white">{step.title}</h3>
              <p className="mt-2.5 text-sm leading-relaxed text-slate-500 dark:text-slate-400">{step.description}</p>
              <span className="absolute -right-10 -top-10 h-28 w-28 rounded-full bg-brand-gradient opacity-10 blur-2xl" />
            </motion.div>
          ))}
        </div>
      </div>
    </section>
  );
}
