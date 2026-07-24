import { FiGithub, FiTwitter, FiLinkedin } from "react-icons/fi";
import Logo from "../ui/Logo.jsx";
import { FOOTER_LINKS } from "../../constants";

export default function Footer() {
  return (
    <footer className="border-t border-slate-200 bg-slate-50 dark:border-white/5 dark:bg-night-900/40">
      <div className="mx-auto max-w-7xl px-5 py-14 sm:px-8">
        <div className="grid gap-10 md:grid-cols-[1.4fr_repeat(4,1fr)]">
          <div>
            <Logo size="sm" />
            <p className="mt-4 max-w-xs text-sm leading-relaxed text-slate-500">
              The intelligent multi-agent platform for placement readiness, recruitment and career
              success.
            </p>
            <div className="mt-5 flex items-center gap-3">
              {[FiGithub, FiTwitter, FiLinkedin].map((Icon, index) => (
                <a
                  key={index}
                  href="#"
                  aria-label="Social link"
                  className="glass flex h-9 w-9 items-center justify-center rounded-lg text-slate-500 dark:text-slate-400 transition-colors hover:border-primary-500/40 hover:text-slate-900 dark:hover:text-white"
                >
                  <Icon size={16} />
                </a>
              ))}
            </div>
          </div>

          {FOOTER_LINKS.map((column) => (
            <div key={column.heading}>
              <h4 className="text-sm font-semibold text-slate-900 dark:text-white">{column.heading}</h4>
              <ul className="mt-4 space-y-2.5">
                {column.links.map((link) => (
                  <li key={link}>
                    <a href="#" className="text-sm text-slate-500 transition-colors hover:text-slate-700 dark:hover:text-slate-200">
                      {link}
                    </a>
                  </li>
                ))}
              </ul>
            </div>
          ))}
        </div>

        <div className="mt-12 flex flex-col items-center justify-between gap-4 border-t border-slate-200 dark:border-white/5 pt-6 sm:flex-row">
          <p className="text-xs text-slate-600">
            © {new Date().getFullYear()} PlaceNextAI. All rights reserved.
          </p>
          <p className="text-xs text-slate-600">Built for students. Trusted by recruiters.</p>
        </div>
      </div>
    </footer>
  );
}
