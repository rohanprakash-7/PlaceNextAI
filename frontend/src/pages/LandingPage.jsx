import { motion } from "framer-motion";
import Navbar from "../components/layout/Navbar.jsx";
import Footer from "../components/layout/Footer.jsx";
import Hero from "../components/landing/Hero.jsx";
import Features from "../components/landing/Features.jsx";
import HowItWorks from "../components/landing/HowItWorks.jsx";
import Stats from "../components/landing/Stats.jsx";
import CTA from "../components/landing/CTA.jsx";

export default function LandingPage() {
  return (
    <motion.div
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      exit={{ opacity: 0 }}
      transition={{ duration: 0.35 }}
    >
      <Navbar />
      <main>
        <Hero />
        <Features />
        <HowItWorks />
        <Stats />
        <CTA />
      </main>
      <Footer />
    </motion.div>
  );
}
