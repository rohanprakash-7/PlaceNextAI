import { useRef, useState } from "react";
import { FiUploadCloud, FiFileText, FiLoader, FiX } from "react-icons/fi";
import GradientButton from "../ui/GradientButton.jsx";

export default function ResumeUploadCard({ onUpload, uploading }) {
  const inputRef = useRef(null);
  const [file, setFile] = useState(null);
  const [jobDescription, setJobDescription] = useState("");
  const [dragOver, setDragOver] = useState(false);

  const pick = (picked) => {
    if (picked && picked.type === "application/pdf") {
      setFile(picked);
    }
  };

  const handleSubmit = (event) => {
    event.preventDefault();
    if (file) onUpload(file, jobDescription);
  };

  return (
    <form onSubmit={handleSubmit} className="glass-card p-6">
      <h2 className="font-display text-lg font-semibold text-white">Analyze a resume</h2>
      <p className="mt-1 text-xs text-slate-500">
        Upload a PDF. Add a target job description for a keyword-matched score.
      </p>

      <div
        onClick={() => inputRef.current?.click()}
        onDragOver={(event) => {
          event.preventDefault();
          setDragOver(true);
        }}
        onDragLeave={() => setDragOver(false)}
        onDrop={(event) => {
          event.preventDefault();
          setDragOver(false);
          pick(event.dataTransfer.files?.[0]);
        }}
        className={
          "mt-5 flex cursor-pointer flex-col items-center justify-center rounded-2xl border-2 border-dashed px-6 py-10 text-center transition-colors " +
          (dragOver
            ? "border-primary-500/70 bg-primary-500/10"
            : "border-white/10 bg-white/[0.02] hover:border-primary-500/40")
        }
      >
        <input
          ref={inputRef}
          type="file"
          accept="application/pdf"
          className="hidden"
          onChange={(event) => pick(event.target.files?.[0])}
        />
        {file ? (
          <div className="flex items-center gap-3">
            <FiFileText className="text-primary-400" size={22} />
            <span className="text-sm font-medium text-white">{file.name}</span>
            <button
              type="button"
              aria-label="Remove file"
              onClick={(event) => {
                event.stopPropagation();
                setFile(null);
              }}
              className="glass flex h-7 w-7 items-center justify-center rounded-lg text-slate-400 hover:text-rose-400"
            >
              <FiX size={13} />
            </button>
          </div>
        ) : (
          <>
            <FiUploadCloud className="text-slate-500" size={28} />
            <p className="mt-3 text-sm text-slate-300">Drop your PDF here or click to browse</p>
            <p className="mt-1 text-xs text-slate-500">Text-based PDF · max 5 MB</p>
          </>
        )}
      </div>

      <label className="mt-4 block">
        <span className="mb-1.5 block text-xs font-medium text-slate-400">
          Target job description (optional)
        </span>
        <textarea
          value={jobDescription}
          onChange={(event) => setJobDescription(event.target.value)}
          rows={4}
          placeholder="Paste the JD you're targeting…"
          className="input-glass !min-h-24 !pl-4"
        />
      </label>

      <div className="mt-5">
        <GradientButton type="submit" disabled={!file || uploading} className="w-full">
          {uploading ? (
            <>
              <FiLoader className="animate-spin" size={15} /> Analyzing…
            </>
          ) : (
            <>
              <FiUploadCloud size={15} /> Upload & analyze
            </>
          )}
        </GradientButton>
      </div>
    </form>
  );
}
