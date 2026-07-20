import { Link } from "react-router-dom";
import { FiZap } from "react-icons/fi";
import { APP_NAME } from "../../constants";

export default function Logo({ to = "/", size = "md" }) {
  const iconSize = size === "sm" ? "h-7 w-7" : "h-9 w-9";
  const textSize = size === "sm" ? "text-base" : "text-lg";

  return (
    <Link to={to} className="group inline-flex items-center gap-2.5">
      <span
        className={
          iconSize +
          " flex items-center justify-center rounded-xl bg-brand-gradient text-white shadow-glow-sm transition-transform duration-300 group-hover:scale-105"
        }
      >
        <FiZap size={18} />
      </span>
      <span className={textSize + " font-display font-semibold tracking-tight text-white"}>
        PlaceNext<span className="text-gradient">AI</span>
        <span className="sr-only">{APP_NAME}</span>
      </span>
    </Link>
  );
}
