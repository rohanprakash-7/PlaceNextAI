import { useEffect, useState } from "react";
import { motion, AnimatePresence } from "framer-motion";
import { FiMenu, FiX, FiArrowRight } from "react-icons/fi";
import { Link } from "react-router-dom";
import Logo from "../ui/Logo.jsx";
import GradientButton from "../ui/GradientButton.jsx";
import { NAV_LINKS } from "../../constants";

export default function Navbar() {
  const [scrolled, setScrolled] = useState(false);
  const [menuOpen, setMenuOpen] = useState(false);

  useEffect(() => {
    const onScroll = () => setScrolled(window.scrollY > 24);
    onScroll();
    window.addEventListener("scroll", onScroll);
    return () => window.removeEventListener("scroll", onScroll);
  }, []);

  return (
    <motion.header
      initial={{ y: -70, opacity: 0 }}
      animate={{ y: 0, opacity: 1 }}
      transition={{ duration: 0.6, ease: [0.21, 0.47, 0.32, 0.98] }}
      className={
        "fixed inset-x-0 top-0 z-50 transition-all duration-300 " +
        (scrolled ? "glass-strong shadow-lg shadow-black/20" : "bg-transparent")
      }
    >
      <nav className="mx-auto flex h-16 max-w-7xl items-center justify-between px-5 sm:px-8">
        <Logo />

        <div className="hidden items-center gap-8 md:flex">
          {NAV_LINKS.map((link) => (
            <a key={link.label} href={link.href} className="nav-link">
              {link.label}
            </a>
          ))}
        </div>

        <div className="hidden items-center gap-3 md:flex">
          <Link to="/login" className="nav-link px-3 py-2">
            Log in
          </Link>
          <GradientButton to="/register" className="!px-5 !py-2.5">
            Get started <FiArrowRight size={15} />
          </GradientButton>
        </div>

        <button
          type="button"
          aria-label="Toggle navigation menu"
          onClick={() => setMenuOpen((open) => !open)}
          className="glass flex h-10 w-10 items-center justify-center rounded-xl text-slate-200 md:hidden"
        >
          {menuOpen ? <FiX size={18} /> : <FiMenu size={18} />}
        </button>
      </nav>

      <AnimatePresence>
        {menuOpen && (
          <motion.div
            initial={{ opacity: 0, height: 0 }}
            animate={{ opacity: 1, height: "auto" }}
            exit={{ opacity: 0, height: 0 }}
            transition={{ duration: 0.28, ease: "easeInOut" }}
            className="glass-strong overflow-hidden border-t border-white/5 md:hidden"
          >
            <div className="flex flex-col gap-1 px-5 py-4">
              {NAV_LINKS.map((link) => (
                <a
                  key={link.label}
                  href={link.href}
                  onClick={() => setMenuOpen(false)}
                  className="rounded-lg px-3 py-2.5 text-sm font-medium text-slate-300 hover:bg-white/5 hover:text-white"
                >
                  {link.label}
                </a>
              ))}
              <div className="mt-3 flex flex-col gap-2 border-t border-white/5 pt-4">
                <Link
                  to="/login"
                  className="rounded-lg px-3 py-2.5 text-center text-sm font-medium text-slate-300 hover:bg-white/5 hover:text-white"
                >
                  Log in
                </Link>
                <GradientButton to="/register" className="w-full">
                  Get started <FiArrowRight size={15} />
                </GradientButton>
              </div>
            </div>
          </motion.div>
        )}
      </AnimatePresence>
    </motion.header>
  );
}
