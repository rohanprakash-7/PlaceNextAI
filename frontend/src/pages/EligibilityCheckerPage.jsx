import { useCallback, useEffect, useState } from "react";
import { motion } from "framer-motion";
import { Link } from "react-router-dom";
import { FiCheckCircle, FiXCircle, FiTarget, FiTrendingUp } from "react-icons/fi";
import DashboardLayout from "../components/dashboard/DashboardLayout.jsx";
import EmptyState from "../components/ui/EmptyState.jsx";
import ErrorState from "../components/ui/ErrorState.jsx";
import GradientButton from "../components/ui/GradientButton.jsx";
import { SkeletonBlock } from "../components/ui/Skeleton.jsx";
import { STUDENT_NAV } from "../constants";
import { useAuth } from "../context/AuthContext.jsx";
import { getEligibilityCompanies, getEligibilityForCompany } from "../services/eligibilityService";

export default function EligibilityCheckerPage() {
  const { user } = useAuth();

  const [companies, setCompanies] = useState([]);
  const [selected, setSelected] = useState("");
  const [results, setResults] = useState(null);
  const [loading, setLoading] = useState(true);
  const [checking, setChecking] = useState(false);
  const [error, setError] = useState("");

  const bootstrap = useCallback(async () => {
    setLoading(true);
    setError("");
    try {
      setCompanies(await getEligibilityCompanies());
    } catch (err) {
      setError(err.friendlyMessage || "Failed to load companies");
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    bootstrap();
  }, [bootstrap]);

  const handleCheck = async (company) => {
    setSelected(company);
    setChecking(true);
    setError("");
    try {
      setResults(await getEligibilityForCompany(company));
    } catch (err) {
      setError(err.friendlyMessage || "Could not check eligibility for that company");
      setResults(null);
    } finally {
      setChecking(false);
    }
  };

  return (
    <DashboardLayout navItems={STUDENT_NAV} roleLabel="Student" title="Eligibility Checker" userName={user?.name || "Student"}>
      {loading && <SkeletonBlock className="h-64" />}

      {!loading && (
        <div className="grid gap-5 lg:grid-cols-3">
          <div className="glass-card p-6 lg:col-span-1">
            <div className="flex items-center gap-2">
              <FiTarget className="text-primary-400" size={16} />
              <h2 className="font-display text-lg font-semibold text-slate-900 dark:text-white">Check a company</h2>
            </div>
            <p className="mt-1 text-xs text-slate-500">
              Compares your profile's skills (and CGPA, when a job sets a cutoff) against each open role at the
              company, using live job postings on the platform.
            </p>

            {companies.length === 0 && !error && (
              <p className="mt-4 text-xs text-slate-500">No job postings on the platform yet.</p>
            )}

            <div className="mt-4 flex flex-wrap gap-2">
              {companies.map((company) => (
                <button
                  key={company}
                  type="button"
                  onClick={() => handleCheck(company)}
                  className={
                    "rounded-full px-3.5 py-1.5 text-xs font-medium transition-colors " +
                    (selected === company
                      ? "bg-brand-gradient text-white"
                      : "glass text-slate-500 hover:text-slate-900 dark:text-slate-400 dark:hover:text-white")
                  }
                >
                  {company}
                </button>
              ))}
            </div>
          </div>

          <div className="space-y-4 lg:col-span-2">
            {error && (
              <div className="rounded-xl border border-rose-500/30 bg-rose-500/10 px-4 py-3 text-sm text-rose-300" role="alert">
                {error}
              </div>
            )}

            {checking && <SkeletonBlock className="h-48" />}

            {!checking && !results && !error && (
              <div className="glass-card">
                <EmptyState
                  icon={FiCheckCircle}
                  title="Pick a company to check eligibility"
                  message="Select a company on the left to see which of its open roles you're eligible for right now."
                />
              </div>
            )}

            {!checking &&
              results &&
              results.map((job, index) => (
                <motion.div
                  key={job.jobId}
                  initial={{ opacity: 0, y: 14 }}
                  animate={{ opacity: 1, y: 0 }}
                  transition={{ delay: index * 0.06 }}
                  className="glass-card p-5"
                >
                  <div className="flex flex-wrap items-start justify-between gap-3">
                    <div>
                      <p className="font-display text-base font-semibold text-slate-900 dark:text-white">{job.jobTitle}</p>
                      <p className="text-xs text-slate-500">{job.company}</p>
                    </div>
                    <span
                      className={
                        "inline-flex items-center gap-1.5 rounded-full px-3 py-1 text-xs font-semibold " +
                        (job.overallEligible
                          ? "bg-emerald-500/10 text-emerald-400"
                          : "bg-rose-500/10 text-rose-400")
                      }
                    >
                      {job.overallEligible ? <FiCheckCircle size={13} /> : <FiXCircle size={13} />}
                      {job.overallEligible ? "Eligible" : "Not eligible yet"}
                    </span>
                  </div>

                  <div className="mt-4">
                    <div className="flex items-center justify-between text-xs">
                      <span className="text-slate-500">Skill match</span>
                      <span className="font-semibold text-slate-900 dark:text-white">{job.matchPercent}%</span>
                    </div>
                    <div className="mt-1.5 h-2 overflow-hidden rounded-full bg-slate-200 dark:bg-white/5">
                      <div
                        className={
                          "h-full rounded-full " + (job.skillsEligible ? "bg-brand-gradient" : "bg-rose-500/70")
                        }
                        style={{ width: job.matchPercent + "%" }}
                      />
                    </div>
                  </div>

                  <div className="mt-3 flex items-center justify-between text-xs">
                    <span className="text-slate-500">Estimated placement chance</span>
                    <span
                      className={
                        "font-semibold " +
                        (job.probabilityLabel === "High"
                          ? "text-emerald-500"
                          : job.probabilityLabel === "Medium"
                            ? "text-amber-500"
                            : "text-rose-400")
                      }
                    >
                      {job.successProbability}% · {job.probabilityLabel}
                    </span>
                  </div>

                  {job.requiredCgpa != null && (
                    <div className="mt-3 flex items-center justify-between text-xs">
                      <span className="text-slate-500">CGPA requirement</span>
                      <span className={job.cgpaEligible ? "font-semibold text-emerald-500" : "font-semibold text-rose-400"}>
                        {job.studentCgpa ?? "—"} / {job.requiredCgpa} required
                      </span>
                    </div>
                  )}

                  {job.matchedSkills.length > 0 && (
                    <div className="mt-3">
                      <p className="text-[11px] uppercase tracking-wider text-slate-500">You have</p>
                      <div className="mt-1.5 flex flex-wrap gap-1.5">
                        {job.matchedSkills.map((skill) => (
                          <span
                            key={skill}
                            className="rounded-full bg-emerald-500/10 px-2.5 py-1 text-[11px] font-medium text-emerald-500"
                          >
                            {skill}
                          </span>
                        ))}
                      </div>
                    </div>
                  )}

                  {job.missingSkills.length > 0 && (
                    <div className="mt-3">
                      <p className="text-[11px] uppercase tracking-wider text-slate-500">Missing</p>
                      <div className="mt-1.5 flex flex-wrap gap-1.5">
                        {job.missingSkills.map((skill) => (
                          <span
                            key={skill}
                            className="rounded-full bg-rose-500/10 px-2.5 py-1 text-[11px] font-medium text-rose-400"
                          >
                            {skill}
                          </span>
                        ))}
                      </div>
                    </div>
                  )}

                  {!job.overallEligible && (
                    <Link to="/dashboard/student/roadmap" className="mt-4 inline-block">
                      <GradientButton variant="ghost" className="!px-4 !py-2 text-xs">
                        <FiTrendingUp size={13} /> Close the gap with a roadmap
                      </GradientButton>
                    </Link>
                  )}
                </motion.div>
              ))}
          </div>
        </div>
      )}
    </DashboardLayout>
  );
}
