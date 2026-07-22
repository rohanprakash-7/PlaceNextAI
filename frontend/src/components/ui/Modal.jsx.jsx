import { AnimatePresence, motion } from "framer-motion";
import { FiX } from "react-icons/fi";

export default function Modal({ open, onClose, title, children, maxWidth = "max-w-2xl" }) {
  return (
    <AnimatePresence>
      {open && (
        <>
          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            transition={{ duration: 0.2 }}
            onClick={onClose}
            className="fixed inset-0 z-[70] bg-black/70 backdrop-blur-sm"
          />
          <div className="fixed inset-0 z-[75] flex items-center justify-center overflow-y-auto p-4">
            <motion.div
              initial={{ opacity: 0, y: 28, scale: 0.97 }}
              animate={{ opacity: 1, y: 0, scale: 1 }}
              exit={{ opacity: 0, y: 20, scale: 0.97 }}
              transition={{ duration: 0.3, ease: [0.21, 0.47, 0.32, 0.98] }}
              className={"glass-strong my-auto w-full rounded-2xl border border-white/10 " + maxWidth}
            >
              <div className="flex items-center justify-between border-b border-white/5 px-6 py-4">
                <h2 className="font-display text-lg font-semibold text-white">{title}</h2>
                <button
                  type="button"
                  aria-label="Close dialog"
                  onClick={onClose}
                  className="glass flex h-8 w-8 items-center justify-center rounded-lg text-slate-300 transition-colors hover:text-white"
                >
                  <FiX size={15} />
                </button>
              </div>
              <div className="max-h-[75vh] overflow-y-auto p-6">{children}</div>
            </motion.div>
          </div>
        </>
      )}
    </AnimatePresence>
  );
}
