import { useCallback, useEffect, useState } from "react";
import { motion } from "framer-motion";
import { FiBriefcase, FiLoader, FiPlus } from "react-icons/fi";
import DashboardLayout from "../components/dashboard/DashboardLayout.jsx";
import EmptyState from "../components/ui/EmptyState.jsx";
import GradientButton from "../components/ui/GradientButton.jsx";
import { SkeletonBlock } from "../components/ui/Skeleton.jsx";
import { RECRUITER_NAV } from "../constants";
import { useAuth } from "../context/AuthContext.jsx";
import { useToast } from "../context/ToastContext.jsx";
import { createJob, getJobsForRecruiter } from "../services/jobService";

const EMPTY_FORM = {
  title: "",
  location: "",
  description: "",
  salary: "",
  skillsRequired: "",
  minCgpa: "",
};

export default function RecruiterJobPostingsPage() {
  const { user } = useAuth();
  const toast = useToast();

  const [jobs, setJobs] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [creating, setCreating] = useState(false);
  const [form, setForm] = useState(EMPTY_FORM);

  const load = useCallback(async () => {
    setLoading(true);
    setError("");
    try {
      setJobs(await getJobsForRecruiter());
    } catch (err) {
      setError(err.friendlyMessage || "Failed to load your job postings");
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    load();
  }, [load]);

  const handleCreate = async (event) => {
    event.preventDefault();
    setCreating(true);
    setError("");
    try {
      await createJob({
        title: form.title,
        location: form.location,
        description: form.description,
        salary: form.salary,
        skillsRequired: form.skillsRequired,
        minCgpa: form.minCgpa === "" ? null : Number(form.minCgpa),
      });
      setForm(EMPTY_FORM);
      toast.success("Job posting published — students can now see it in eligibility checks and applications.");
      await load();
    } catch (err) {
      setError(err.friendlyMessage || "Could not publish the job posting");
    } finally {
      setCreating(false);
    }
  };

  return (
    <DashboardLayout navItems={RECRUITER_NAV} roleLabel="Recruiter" title="Job Postings" userName={user?.name || "Recruiter"}>
      <div className="glass-card p-6">
        <div className="flex items-center gap-2">
          <FiBriefcase className="text-primary-400" size={16} />
          <h2 className="font-display text-lg font-semibold text-slate-900 dark:text-white">Post a new role</h2>
        </div>
        <p className="mt-1 text-xs text-slate-500">
          Published under your company profile. Skills and CGPA cutoff drive the student eligibility checker
          automatically.
        </p>

        <form onSubmit={handleCreate} className="mt-4 grid gap-3 sm:grid-cols-2">
          <input
            type="text"
            required
            placeholder="Job title (e.g. Backend Engineer Intern)"
            value={form.title}
            onChange={(event) => setForm({ ...form, title: event.target.value })}
            className="input-glass sm:col-span-2"
          />
          <input
            type="text"
            placeholder="Location (e.g. Bengaluru, India / Remote)"
            value={form.location}
            onChange={(event) => setForm({ ...form, location: event.target.value })}
            className="input-glass"
          />
          <input
            type="text"
            placeholder="Salary (e.g. 8-12 LPA)"
            value={form.salary}
            onChange={(event) => setForm({ ...form, salary: event.target.value })}
            className="input-glass"
          />
          <textarea
            required
            rows={3}
            placeholder="Role description"
            value={form.description}
            onChange={(event) => setForm({ ...form, description: event.target.value })}
            className="input-glass sm:col-span-2"
          />
          <input
            type="text"
            placeholder="Required skills, comma-separated (e.g. Java,SQL,System Design)"
            value={form.skillsRequired}
            onChange={(event) => setForm({ ...form, skillsRequired: event.target.value })}
            className="input-glass sm:col-span-2"
          />
          <input
            type="number"
            step="0.1"
            min="0"
            max="10"
            placeholder="Minimum CGPA (optional)"
            value={form.minCgpa}
            onChange={(event) => setForm({ ...form, minCgpa: event.target.value })}
            className="input-glass"
          />
          <GradientButton type="submit" disabled={creating} className="justify-center">
            {creating ? (
              <>
                <FiLoader className="animate-spin" size={16} /> Publishing…
              </>
            ) : (
              <>
                <FiPlus size={16} /> Publish job
              </>
            )}
          </GradientButton>
        </form>

        {error && (
          <p className="mt-3 rounded-lg border border-rose-500/30 bg-rose-500/10 px-3 py-2 text-xs text-rose-300">
            {error}
          </p>
        )}
      </div>

      <div className="mt-5">
        {loading && <SkeletonBlock className="h-48" />}

        {!loading && !error && jobs.length === 0 && (
          <div className="glass-card">
            <EmptyState
              icon={FiBriefcase}
              title="No job postings yet"
              message="Publish your first role above — students will see it in the eligibility checker and can apply."
            />
          </div>
        )}

        {!loading && jobs.length > 0 && (
          <div className="grid gap-4 sm:grid-cols-2">
            {jobs.map((job, index) => (
              <motion.div
                key={job.id}
                initial={{ opacity: 0, y: 12 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ delay: index * 0.05 }}
                className="glass-card p-5"
              >
                <div className="flex items-start justify-between gap-3">
                  <div>
                    <p className="font-display text-base font-semibold text-slate-900 dark:text-white">{job.title}</p>
                    <p className="text-xs text-slate-500">
                      {job.company}
                      {job.location ? " · " + job.location : ""}
                    </p>
                  </div>
                  {job.salary && (
                    <span className="rounded-full bg-emerald-500/10 px-2.5 py-1 text-[11px] font-semibold text-emerald-500">
                      {job.salary}
                    </span>
                  )}
                </div>

                <p className="mt-3 line-clamp-3 text-xs text-slate-600 dark:text-slate-400">{job.description}</p>

                {job.skillsRequired && (
                  <div className="mt-3 flex flex-wrap gap-1.5">
                    {job.skillsRequired.split(",").map((skill) => (
                      <span
                        key={skill}
                        className="rounded-full bg-primary-500/10 px-2.5 py-1 text-[11px] font-medium text-primary-400"
                      >
                        {skill.trim()}
                      </span>
                    ))}
                  </div>
                )}

                <div className="mt-3 flex items-center justify-between text-[11px] text-slate-500">
                  <span>Posted {job.createdDate ? new Date(job.createdDate).toLocaleDateString() : ""}</span>
                  {job.minCgpa != null && <span>Min CGPA {job.minCgpa}</span>}
                </div>
              </motion.div>
            ))}
          </div>
        )}
      </div>
    </DashboardLayout>
  );
}
