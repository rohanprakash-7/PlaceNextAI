import { useCallback, useEffect, useMemo, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { motion } from "framer-motion";
import {
  FiMic,
  FiMicOff,
  FiVolume2,
  FiSend,
  FiLoader,
  FiCheckCircle,
  FiArrowRight,
  FiFlag,
} from "react-icons/fi";
import DashboardLayout from "../components/dashboard/DashboardLayout.jsx";
import ErrorState from "../components/ui/ErrorState.jsx";
import GradientButton from "../components/ui/GradientButton.jsx";
import { SkeletonBlock } from "../components/ui/Skeleton.jsx";
import { STUDENT_NAV } from "../constants";
import { useAuth } from "../context/AuthContext.jsx";
import { useSpeechSynthesis } from "../hooks/useSpeechSynthesis.js";
import { useSpeechRecognition } from "../hooks/useSpeechRecognition.js";
import {
  getInterviewSession,
  submitInterviewAnswer,
  completeInterview,
} from "../services/interviewService";

const CATEGORY_LABELS = {
  BEHAVIORAL: "Behavioral",
  TECHNICAL: "Technical",
  COMPANY_FIT: "Company fit",
};

function ScoreRing({ score }) {
  const color = score >= 80 ? "#34d399" : score >= 60 ? "#8b5cf6" : score >= 40 ? "#fbbf24" : "#fb7185";
  return (
    <div className="relative flex h-28 w-28 items-center justify-center rounded-full" style={{
      background: `conic-gradient(${color} ${score * 3.6}deg, rgba(148,163,184,0.15) 0deg)`,
    }}>
      <div className="flex h-[88px] w-[88px] items-center justify-center rounded-full bg-white dark:bg-night-900">
        <span className="font-display text-2xl font-bold text-slate-900 dark:text-white">{score}</span>
      </div>
    </div>
  );
}

export default function InterviewSessionPage() {
  const { sessionId } = useParams();
  const navigate = useNavigate();
  const { user } = useAuth();

  const [session, setSession] = useState(null);
  const [currentIndex, setCurrentIndex] = useState(0);
  const [answerText, setAnswerText] = useState("");
  const [lastResult, setLastResult] = useState(null);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [completing, setCompleting] = useState(false);
  const [error, setError] = useState("");
  const [hasSpokenCurrent, setHasSpokenCurrent] = useState(false);

  const { supported: ttsSupported, speak } = useSpeechSynthesis();
  const {
    supported: sttSupported,
    isListening,
    transcript,
    error: micError,
    start: startListening,
    stop: stopListening,
    reset: resetTranscript,
  } = useSpeechRecognition();

  const load = useCallback(async () => {
    setLoading(true);
    setError("");
    try {
      const data = await getInterviewSession(sessionId);
      setSession(data);
      const firstUnanswered = data.questions.findIndex((question) => !question.answered);
      setCurrentIndex(firstUnanswered === -1 ? data.questions.length - 1 : firstUnanswered);
    } catch (err) {
      setError(err.friendlyMessage || "Failed to load this interview");
    } finally {
      setLoading(false);
    }
  }, [sessionId]);

  useEffect(() => {
    load();
  }, [load]);

  const currentQuestion = session?.questions?.[currentIndex];
  const allAnswered = useMemo(
    () => session?.questions?.every((question) => question.answered) ?? false,
    [session]
  );
  const isComplete = session?.status === "COMPLETED";

  useEffect(() => {
    if (!currentQuestion || currentQuestion.answered || isComplete) return;
    setAnswerText("");
    setLastResult(null);
    resetTranscript();
    setHasSpokenCurrent(false);
  }, [currentQuestion?.id]); // eslint-disable-line react-hooks/exhaustive-deps

  useEffect(() => {
    if (ttsSupported && currentQuestion && !currentQuestion.answered && !isComplete && !hasSpokenCurrent) {
      speak(currentQuestion.questionText);
      setHasSpokenCurrent(true);
    }
  }, [currentQuestion, ttsSupported, isComplete, hasSpokenCurrent, speak]);

  useEffect(() => {
    if (isListening) {
      setAnswerText(transcript);
    }
  }, [transcript, isListening]);

  const handleToggleMic = () => {
    if (isListening) {
      stopListening();
    } else {
      startListening();
    }
  };

  const handleReplay = () => {
    if (currentQuestion) {
      speak(currentQuestion.questionText);
    }
  };

  const handleSubmitAnswer = async (event) => {
    event.preventDefault();
    if (!answerText.trim() || !currentQuestion) return;
    if (isListening) stopListening();

    setSubmitting(true);
    setError("");
    try {
      const result = await submitInterviewAnswer(sessionId, currentQuestion.id, answerText.trim());
      setLastResult(result);
      setSession((prev) => ({
        ...prev,
        questions: prev.questions.map((question) => (question.id === result.id ? result : question)),
      }));
    } catch (err) {
      setError(err.friendlyMessage || "Could not submit your answer");
    } finally {
      setSubmitting(false);
    }
  };

  const handleNext = () => {
    if (currentIndex < session.questions.length - 1) {
      setCurrentIndex((index) => index + 1);
    }
  };

  const handleFinish = async () => {
    setCompleting(true);
    setError("");
    try {
      const completed = await completeInterview(sessionId);
      setSession(completed);
    } catch (err) {
      setError(err.friendlyMessage || "Could not finish the interview");
    } finally {
      setCompleting(false);
    }
  };

  const isLastQuestion = session && currentIndex === session.questions.length - 1;

  return (
    <DashboardLayout navItems={STUDENT_NAV} roleLabel="Student" title="Mock Interview" userName={user?.name || "Student"}>
      {loading && <SkeletonBlock className="h-96" />}

      {!loading && error && !session && (
        <div className="glass-card">
          <ErrorState message={error} onRetry={load} />
        </div>
      )}

      {!loading && session && isComplete && (
        <motion.div initial={{ opacity: 0, y: 16 }} animate={{ opacity: 1, y: 0 }} className="glass-card p-6">
          <div className="flex flex-col items-center gap-4 text-center sm:flex-row sm:text-left">
            <ScoreRing score={session.overallScore ?? 0} />
            <div>
              <h2 className="font-display text-xl font-semibold text-slate-900 dark:text-white">Interview complete</h2>
              <p className="mt-1 text-sm text-slate-500">
                {session.targetCompany ? "Target: " + session.targetCompany : "General interview"} ·{" "}
                {session.questions.length} questions
              </p>
              <p className="mt-1 text-xs text-slate-400 dark:text-slate-600">
                Your overall score feeds your Readiness Score and unlocks interview badges.
              </p>
            </div>
          </div>

          <div className="mt-6 space-y-3">
            {session.questions.map((question, index) => (
              <div key={question.id} className="rounded-xl border border-slate-200 bg-white/60 p-4 dark:border-white/5 dark:bg-white/[0.03]">
                <div className="flex items-center justify-between gap-3">
                  <p className="text-xs font-semibold uppercase tracking-wider text-primary-500">
                    Q{index + 1} · {CATEGORY_LABELS[question.category] || question.category}
                  </p>
                  <span className="rounded-full bg-primary-500/10 px-2.5 py-1 text-xs font-semibold text-primary-500">
                    {question.score}/100
                  </span>
                </div>
                <p className="mt-2 text-sm font-medium text-slate-900 dark:text-white">{question.questionText}</p>
                <p className="mt-2 text-sm text-slate-500">{question.studentAnswer}</p>
                <p className="mt-2 text-xs text-slate-400 dark:text-slate-600">{question.feedback}</p>
              </div>
            ))}
          </div>

          <GradientButton onClick={() => navigate("/dashboard/student/interviews")} className="mt-6 w-full">
            Back to interviews
          </GradientButton>
        </motion.div>
      )}

      {!loading && session && !isComplete && currentQuestion && (
        <>
          <div className="mb-4 flex items-center justify-between text-xs text-slate-500">
            <span>
              Question {currentIndex + 1} of {session.questions.length}
            </span>
            <span className="rounded-full bg-primary-500/10 px-2.5 py-1 font-semibold text-primary-500">
              {CATEGORY_LABELS[currentQuestion.category] || currentQuestion.category}
            </span>
          </div>

          {error && (
            <p className="mb-4 rounded-lg border border-rose-500/30 bg-rose-500/10 px-3 py-2 text-xs text-rose-300">
              {error}
            </p>
          )}

          <motion.div
            key={currentQuestion.id}
            initial={{ opacity: 0, y: 12 }}
            animate={{ opacity: 1, y: 0 }}
            className="glass-card p-6"
          >
            <div className="flex items-start justify-between gap-3">
              <p className="font-display text-lg font-semibold text-slate-900 dark:text-white">
                {currentQuestion.questionText}
              </p>
              {ttsSupported && (
                <button
                  type="button"
                  onClick={handleReplay}
                  aria-label="Replay question"
                  className="glass flex h-9 w-9 shrink-0 items-center justify-center rounded-xl text-primary-500"
                >
                  <FiVolume2 size={15} />
                </button>
              )}
            </div>

            {!currentQuestion.answered ? (
              <form onSubmit={handleSubmitAnswer} className="mt-5">
                <div className="flex items-center gap-3">
                  {sttSupported && (
                    <button
                      type="button"
                      onClick={handleToggleMic}
                      aria-label={isListening ? "Stop recording" : "Start recording"}
                      className={
                        "flex h-14 w-14 shrink-0 items-center justify-center rounded-full transition-colors " +
                        (isListening
                          ? "animate-pulse bg-rose-500 text-white shadow-glow"
                          : "bg-brand-gradient text-white shadow-glow-sm")
                      }
                    >
                      {isListening ? <FiMicOff size={20} /> : <FiMic size={20} />}
                    </button>
                  )}
                  <textarea
                    value={answerText}
                    onChange={(event) => setAnswerText(event.target.value)}
                    rows={4}
                    placeholder={
                      sttSupported
                        ? "Tap the mic and speak your answer, or type it here…"
                        : "Type your answer here…"
                    }
                    className="input-glass resize-none"
                  />
                </div>
                {isListening && (
                  <p className="mt-2 text-xs text-primary-500">Listening… tap the mic again when you're done.</p>
                )}
                {micError && <p className="mt-2 text-xs text-rose-400">{micError}</p>}

                <GradientButton type="submit" disabled={submitting || !answerText.trim()} className="mt-4 w-full">
                  {submitting ? (
                    <>
                      <FiLoader className="animate-spin" size={16} /> Scoring…
                    </>
                  ) : (
                    <>
                      <FiSend size={15} /> Submit answer
                    </>
                  )}
                </GradientButton>
              </form>
            ) : (
              <div className="mt-5">
                <div className="rounded-xl border border-slate-200 bg-white/60 p-4 dark:border-white/5 dark:bg-white/[0.03]">
                  <p className="text-sm text-slate-600 dark:text-slate-300">{currentQuestion.studentAnswer}</p>
                </div>
                {(lastResult || currentQuestion.score != null) && (
                  <div className="mt-3 flex items-start gap-2.5 rounded-xl border border-primary-500/30 bg-primary-500/5 p-4">
                    <FiCheckCircle className="mt-0.5 shrink-0 text-primary-500" size={16} />
                    <div>
                      <p className="text-sm font-semibold text-slate-900 dark:text-white">
                        Score: {currentQuestion.score}/100
                      </p>
                      <p className="mt-1 text-xs text-slate-500">{currentQuestion.feedback}</p>
                    </div>
                  </div>
                )}

                {!isLastQuestion ? (
                  <GradientButton onClick={handleNext} className="mt-4 w-full">
                    Next question <FiArrowRight size={15} />
                  </GradientButton>
                ) : (
                  <GradientButton onClick={handleFinish} disabled={!allAnswered || completing} className="mt-4 w-full">
                    {completing ? (
                      <>
                        <FiLoader className="animate-spin" size={16} /> Finishing…
                      </>
                    ) : (
                      <>
                        <FiFlag size={15} /> Finish interview
                      </>
                    )}
                  </GradientButton>
                )}
              </div>
            )}
          </motion.div>
        </>
      )}
    </DashboardLayout>
  );
}
