import GlassCard from "../ui/GlassCard.jsx";
import SectionHeading from "../ui/SectionHeading.jsx";
import { FEATURES } from "../../constants";

export default function Features() {
  return (
    <section id="features" className="relative py-24">
      <div className="mx-auto max-w-7xl px-5 sm:px-8">
        <SectionHeading
          eyebrow="Platform"
          title="Every agent your career"
          highlight="needs to win"
          description="Six specialized AI agents work together across your entire placement journey — from the first resume draft to the final offer letter."
        />

        <div className="grid gap-5 sm:grid-cols-2 lg:grid-cols-3">
          {FEATURES.map((feature, index) => {
            const Icon = feature.icon;
            return (
              <GlassCard key={feature.title} delay={index * 0.08} className="group p-6">
                <span
                  className={
                    "inline-flex h-11 w-11 items-center justify-center rounded-xl bg-gradient-to-br text-white shadow-glow-sm transition-transform duration-300 group-hover:scale-110 " +
                    feature.accent
                  }
                >
                  <Icon size={19} />
                </span>
                <h3 className="mt-5 font-display text-lg font-semibold text-white">{feature.title}</h3>
                <p className="mt-2.5 text-sm leading-relaxed text-slate-400">{feature.description}</p>
              </GlassCard>
            );
          })}
        </div>
      </div>
    </section>
  );
}
