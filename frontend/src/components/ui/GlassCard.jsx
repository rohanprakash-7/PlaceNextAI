import { motion } from "framer-motion";

export default function GlassCard({
  children,
  className = "",
  hover = true,
  delay = 0,
  ...props
}) {
  return (
    <motion.div
      initial={{ opacity: 0, y: 24 }}
      whileInView={{ opacity: 1, y: 0 }}
      viewport={{ once: true, margin: "-60px" }}
      transition={{ duration: 0.55, delay, ease: [0.21, 0.47, 0.32, 0.98] }}
      whileHover={
        hover
          ? {
              y: -6,
              boxShadow: "0 0 45px -12px rgba(139, 92, 246, 0.35)",
              borderColor: "rgba(139, 92, 246, 0.35)",
            }
          : undefined
      }
      className={"glass-card " + className}
      {...props}
    >
      {children}
    </motion.div>
  );
}
