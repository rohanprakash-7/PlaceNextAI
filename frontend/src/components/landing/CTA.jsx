import { motion } from "framer-motion";
import { FiArrowRight } from "react-icons/fi";
import GradientButton from "../ui/GradientButton.jsx";

export default function CTA() {
  return (
    <section id="recruiters" className="pb-28 pt-8">
      <div className="mx-auto max-w-7xl px-5 sm:px-8">
        <motion.div
          initial={{ opacity: 0, y: 32 }}
          whileInView={{ opacity: 1, y: 0 }}
          viewport={{ once: true, margin: "-80px" }}
          transition={{ duration: 0.7, ease: [0.21, 0.47, 0.32, 0.98] }}
          className="glass-card relative overflow-hidden px-6 py-16 text-center sm:px-14"
        >
          <div className="pointer-events-none absolute -left-24 -top-24 h-64 w-64 rounded-full bg-primary-600/25 blur-3xl" />
          <div className="pointer-events-none absolute -bottom-24 -right-24 h-64 w-64 rounded-full bg-accent-600/25 blur-3xl" />

          <h2 className="relative mx-auto max-w-2xl font-display text-3xl font-semibold tracking-tight text-slate-900 dark:text-white sm:text-4xl">
            Ready to make your next placement season{" "}
            <span className="text-gradient">unstoppable?</span>
          </h2>
          <p className="relative mx-auto mt-4 max-w-xl text-sm leading-relaxed text-slate-500 dark:text-slate-400 sm:text-base">
            Join students sharpening their readiness score every day and recruiters who shortlist
            with confidence, not guesswork.
          </p>
          <div className="relative mt-9 flex flex-col items-center justify-center gap-3 sm:flex-row">
            <GradientButton to="/register">
              Create free account <FiArrowRight size={16} />
            </GradientButton>
            <GradientButton to="/login" variant="ghost">
              Sign in to dashboard
            </GradientButton>
          </div>
        </motion.div>
      </div>
    </section>
  );
}
