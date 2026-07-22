import { useState } from "react";
import { FiLoader, FiSend } from "react-icons/fi";
import Modal from "../ui/Modal.jsx";
import GradientButton from "../ui/GradientButton.jsx";

const RATING_FIELDS = [
  { key: "communicationRating", label: "Communication" },
  { key: "technicalRating", label: "Technical depth" },
  { key: "problemSolvingRating", label: "Problem solving" },
  { key: "cultureFitRating", label: "Culture fit" },
];

const OUTCOME_OPTIONS = [
  { value: "ADVANCED", label: "Advance to next round" },
  { value: "ON_HOLD", label: "On hold" },
  { value: "REJECTED", label: "Not moving forward" },
];

function RatingSlider({ label, value, onChange }) {
  return (
    <div>
      <div className="flex items-center justify-between text-sm">
        <span className="text-slate-300">{label}</span>
        <span className="font-semibold text-white">{value} / 5</span>
      </div>
      <input
        type="range"
        min={1}
        max={5}
        step={1}
        value={value}
        onChange={(event) => onChange(Number(event.target.value))}
        className="mt-2 w-full accent-primary-500"
      />
    </div>
  );
}

export default function FeedbackFormModal({ open, onClose, onSubmit, applicantName, submitting }) {
  const [ratings, setRatings] = useState({
    communicationRating: 3,
    technicalRating: 3,
    problemSolvingRating: 3,
    cultureFitRating: 3,
  });
  const [outcome, setOutcome] = useState("ADVANCED");
  const [comment, setComment] = useState("");

  const handleSubmit = (event) => {
    event.preventDefault();
    onSubmit({ ...ratings, outcome, comment: comment.trim() || undefined });
  };

  return (
    <Modal open={open} onClose={onClose} title={"Feedback for " + applicantName} maxWidth="max-w-lg">
      <form onSubmit={handleSubmit} className="space-y-5">
        {RATING_FIELDS.map((field) => (
          <RatingSlider
            key={field.key}
            label={field.label}
            value={ratings[field.key]}
            onChange={(value) => setRatings({ ...ratings, [field.key]: value })}
          />
        ))}

        <label className="block">
          <span className="mb-1.5 block text-xs font-medium text-slate-400">Outcome</span>
          <select
            value={outcome}
            onChange={(event) => setOutcome(event.target.value)}
            className="input-glass !pl-4 appearance-none bg-night-800"
          >
            {OUTCOME_OPTIONS.map((option) => (
              <option key={option.value} value={option.value} className="bg-night-800">
                {option.label}
              </option>
            ))}
          </select>
        </label>

        <label className="block">
          <span className="mb-1.5 block text-xs font-medium text-slate-400">Comment (optional)</span>
          <textarea
            value={comment}
            onChange={(event) => setComment(event.target.value)}
            rows={3}
            placeholder="One line the student can learn from…"
            className="input-glass !min-h-20 !pl-4"
          />
        </label>

        <p className="text-xs text-slate-500">
          This feedback is shared with the student in aggregate (averaged with other feedback) and
          adjusts their Placement Readiness Score.
        </p>

        <div className="flex justify-end gap-3 pt-1">
          <GradientButton variant="ghost" onClick={onClose} className="!px-5 !py-2.5">
            Cancel
          </GradientButton>
          <GradientButton type="submit" disabled={submitting} className="!px-5 !py-2.5">
            {submitting ? (
              <>
                <FiLoader className="animate-spin" size={15} /> Submitting…
              </>
            ) : (
              <>
                <FiSend size={15} /> Submit feedback
              </>
            )}
          </GradientButton>
        </div>
      </form>
    </Modal>
  );
}
