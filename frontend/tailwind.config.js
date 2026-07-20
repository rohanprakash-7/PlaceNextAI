/** @type {import('tailwindcss').Config} */
export default {
  darkMode: "class",
  content: ["./index.html", "./src/**/*.{js,jsx}"],
  theme: {
    extend: {
      fontFamily: {
        sans: ["Inter", "system-ui", "sans-serif"],
        display: ["Space Grotesk", "Inter", "sans-serif"],
      },
      colors: {
        night: {
          950: "#05050c",
          900: "#0a0a16",
          850: "#0e0e1c",
          800: "#131324",
          700: "#1b1b32",
        },
        primary: {
          400: "#a78bfa",
          500: "#8b5cf6",
          600: "#7c3aed",
        },
        accent: {
          400: "#60a5fa",
          500: "#3b82f6",
          600: "#2563eb",
        },
      },
      boxShadow: {
        glow: "0 0 45px -12px rgba(139, 92, 246, 0.55)",
        "glow-blue": "0 0 45px -12px rgba(59, 130, 246, 0.55)",
        "glow-sm": "0 0 20px -6px rgba(139, 92, 246, 0.45)",
      },
      backgroundImage: {
        "brand-gradient": "linear-gradient(135deg, #8b5cf6 0%, #3b82f6 100%)",
        "grid-pattern":
          "linear-gradient(rgba(139,92,246,0.06) 1px, transparent 1px), linear-gradient(90deg, rgba(139,92,246,0.06) 1px, transparent 1px)",
      },
      backgroundSize: {
        grid: "44px 44px",
      },
      animation: {
        float: "float 6s ease-in-out infinite",
        "float-delayed": "float 7s ease-in-out 1.5s infinite",
        "pulse-slow": "pulse 5s ease-in-out infinite",
      },
      keyframes: {
        float: {
          "0%, 100%": { transform: "translateY(0px)" },
          "50%": { transform: "translateY(-14px)" },
        },
      },
    },
  },
  plugins: [],
};
