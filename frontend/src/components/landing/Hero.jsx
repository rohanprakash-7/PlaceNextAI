import { motion } from "framer-motion";
import { FiArrowRight, FiPlay, FiCheckCircle, FiTrendingUp, FiZap } from "react-icons/fi";
import GradientButton from "../ui/GradientButton.jsx";

const container = {
  hidden: {},
  show: { transition: { staggerChildren: 0.12, delayChildren: 0.15 } },
};

const item = {
  hidden: { opacity: 0, y: 26 },
  show: { opacity: 1, y: 0, transition: { duration: 0.65, ease: [0.21, 0.47, 0.32, 0.98] } },
};

export default function Hero() {
  return (
    <section className="relative overflow-hidden bg-page-glow pb-24 pt-36 sm:pt-40">
      <div className="pointer-events-none absolute inset-0 bg-grid-pattern bg-grid [mask-image:radial-gradient(ellipse_75%_60%_at_50%_35%,black,transparent)]" />

      <div className="relative mx-auto max-w-7xl px-5 sm:px-8">
        <motion.div
          variants={container}
          initial="hidden"
          animate="show"
          className="mx-auto max-w-3xl text-center"
        >
          <motion.span
            variants={item}
            className="glass inline-flex items-center gap-2 rounded-full px-4 py-1.5 text-xs font-medium text-slate-300"
          >
            <span className="h-1.5 w-1.5 animate-pulse rounded-full bg-primary-400" />
            Multi-agent AI for campus placements
          </motion.span>

          <motion.h1
            variants={item}
            className="mt-6 font-display text-4xl font-semibold leading-[1.1] tracking-tight text-white sm:text-6xl"
          >
            Get placement-ready with <span className="text-gradient">AI that trains</span>, matches
            and hires
          </motion.h1>

          <motion.p variants={item} className="mx-auto mt-6 max-w-xl text-base leading-relaxed text-slate-400 sm:text-lg">
            PlaceNextAI analyzes your resume, runs adaptive mock interviews and matches you to the
            right roles — while recruiters shortlist top candidates in seconds.
          </motion.p>

          <motion.div variants={item} className="mt-9 flex flex-col items-center justify-center gap-3 sm:flex-row">
            <GradientButton to="/register">
              Start free as a student <FiArrowRight size={16} />
            </GradientButton>
            <GradientButton to="/login" variant="ghost">
              <FiPlay size={15} /> Recruiter demo
            </GradientButton>
          </motion.div>

          <motion.div variants={item} className="mt-8 flex flex-wrap items-center justify-center gap-x-6 gap-y-2 text-xs text-slate-500">
            <span className="inline-flex items-center gap-1.5">
              <FiCheckCircle className="text-primary-400" size={14} /> No credit card required
            </span>
            <span className="inline-flex items-center gap-1.5">
              <FiCheckCircle className="text-primary-400" size={14} /> Built for engineering campuses
            </span>
            <span className="inline-flex items-center gap-1.5">
              <FiCheckCircle className="text-primary-400" size={14} /> Explainable AI scoring
            </span>
          </motion.div>
        </motion.div>

        <motion.div
          initial={{ opacity: 0, y: 50 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.8, delay: 0.55, ease: [0.21, 0.47, 0.32, 0.98] }}
          className="relative mx-auto mt-16 max-w-4xl"
        >
          <div className="absolute -inset-x-8 -top-8 h-40 bg-brand-gradient opacity-20 blur-3xl" />

          <div className="glass-card relative overflow-hidden p-5 shadow-glow sm:p-7">
            <div className="flex items-center justify-between border-b border-white/5 pb-4">
              <div>
                <p className="text-xs uppercase tracking-widest text-slate-500">Readiness score</p>
                <p className="mt-1 font-display text-3xl font-semibold text-white">
                  87<span className="text-lg text-slate-500">/100</span>
                </p>
              </div>
              <span className="inline-flex items-center gap-1.5 rounded-full bg-emerald-500/10 px-3 py-1.5 text-xs font-semibold text-emerald-400">
                <FiTrendingUp size={13} /> +12 this week
              </span>
            </div>

            <div className="mt-5 grid gap-4 sm:grid-cols-3">
              {[
                { label: "Resume match", value: 92 },
                { label: "Interview skill", value: 81 },
                { label: "DSA coverage", value: 74 },
              ].map((metric, index) => (
                <div key={metric.label} className="rounded-xl bg-white/[0.03] p-4">
                  <div className="flex items-center justify-between text-xs text-slate-400">
                    <span>{metric.label}</span>
                    <span className="font-semibold text-white">{metric.value}%</span>
                  </div>
                  <div className="mt-3 h-1.5 overflow-hidden rounded-full bg-white/5">
                    <motion.div
                      initial={{ width: 0 }}
                      animate={{ width: metric.value + "%" }}
                      transition={{ duration: 1.1, delay: 0.9 + index * 0.15, ease: "easeOut" }}
                      className="h-full rounded-full bg-brand-gradient"
                    />
                  </div>
                </div>
              ))}
            </div>
          </div>

          <motion.div
            className="glass-card absolute -left-6 -top-10 hidden items-center gap-3 p-4 animate-float lg:flex"
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            transition={{ delay: 1.2, duration: 0.6 }}
          >
            <span className="flex h-9 w-9 items-center justify-center rounded-lg bg-primary-500/15 text-primary-400">
              <FiZap size={16} />
            </span>
            <div>
              <p className="text-xs text-slate-400">AI shortlist</p>
              <p className="text-sm font-semibold text-white">Top 5% match</p>
            </div>
          </motion.div>

          <motion.div
            className="glass-card absolute -right-6 -bottom-8 hidden items-center gap-3 p-4 animate-float-delayed lg:flex"
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            transition={{ delay: 1.35, duration: 0.6 }}
          >
            <span className="flex h-9 w-9 items-center justify-center rounded-lg bg-accent-500/15 text-accent-400">
              <FiCheckCircle size={16} />
            </span>
            <div>
              <p className="text-xs text-slate-400">Mock interview</p>
              <p className="text-sm font-semibold text-white">Round cleared</p>
            </div>
          </motion.div>
        </motion.div>
      </div>
    </section>
  );
}
