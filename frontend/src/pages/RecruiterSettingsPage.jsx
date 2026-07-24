import { useCallback, useEffect, useState } from "react";
import { motion } from "framer-motion";
import { FiBriefcase, FiLoader, FiSave } from "react-icons/fi";
import DashboardLayout from "../components/dashboard/DashboardLayout.jsx";
import ErrorState from "../components/ui/ErrorState.jsx";
import GradientButton from "../components/ui/GradientButton.jsx";
import ChangePasswordForm from "../components/settings/ChangePasswordForm.jsx";
import { SkeletonBlock } from "../components/ui/Skeleton.jsx";
import { RECRUITER_NAV } from "../constants";
import { useAuth } from "../context/AuthContext.jsx";
import { getRecruiterProfile, updateRecruiterProfile } from "../services/recruiterProfileService";

export default function RecruiterSettingsPage() {
  const { user } = useAuth();
  const [form, setForm] = useState({ companyName: "", recruiterName: "", designation: "" });
  const [email, setEmail] = useState("");
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");
  const [saved, setSaved] = useState(false);

  const load = useCallback(async () => {
    setLoading(true);
    setError("");
    try {
      const profile = await getRecruiterProfile();
      setEmail(profile.email);
      setForm({
        companyName: profile.companyName || "",
        recruiterName: profile.recruiterName || "",
        designation: profile.designation || "",
      });
    } catch (err) {
      setError(err.friendlyMessage || "Failed to load your profile");
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    load();
  }, [load]);

  const handleChange = (field) => (event) => {
    setForm({ ...form, [field]: event.target.value });
    setSaved(false);
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setSaving(true);
    setError("");
    try {
      await updateRecruiterProfile(form);
      setSaved(true);
    } catch (err) {
      setError(err.friendlyMessage || "Could not save your profile");
    } finally {
      setSaving(false);
    }
  };

  return (
    <DashboardLayout navItems={RECRUITER_NAV} roleLabel="Recruiter" title="Settings" userName={user?.name || "User"}>
      {loading && <SkeletonBlock className="h-96" />}

      {!loading && error && (
        <div className="glass-card mb-5">
          <ErrorState message={error} onRetry={load} />
        </div>
      )}

      {!loading && (
        <div className="grid gap-5 lg:grid-cols-2">
          <motion.div initial={{ opacity: 0, y: 16 }} animate={{ opacity: 1, y: 0 }} className="glass-card p-6">
            <div className="flex items-center gap-2">
              <FiBriefcase className="text-primary-400" size={16} />
              <h2 className="font-display text-lg font-semibold text-slate-900 dark:text-white">Company profile</h2>
            </div>
            <p className="mt-1 text-xs text-slate-500">{email}</p>

            {saved && (
              <p className="mt-4 rounded-lg border border-emerald-500/30 bg-emerald-500/10 px-3 py-2 text-xs text-emerald-500">
                Profile updated.
              </p>
            )}

            <form onSubmit={handleSubmit} className="mt-4 grid gap-3">
              <input
                type="text"
                required
                placeholder="Company name"
                value={form.companyName}
                onChange={handleChange("companyName")}
                className="input-glass"
              />
              <input
                type="text"
                required
                placeholder="Your name"
                value={form.recruiterName}
                onChange={handleChange("recruiterName")}
                className="input-glass"
              />
              <input
                type="text"
                placeholder="Designation"
                value={form.designation}
                onChange={handleChange("designation")}
                className="input-glass"
              />
              <GradientButton type="submit" disabled={saving} className="w-full">
                {saving ? (
                  <>
                    <FiLoader className="animate-spin" size={16} /> Saving…
                  </>
                ) : (
                  <>
                    <FiSave size={15} /> Save profile
                  </>
                )}
              </GradientButton>
            </form>
          </motion.div>

          <motion.div initial={{ opacity: 0, y: 16 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: 0.1 }}>
            <ChangePasswordForm />
          </motion.div>
        </div>
      )}
    </DashboardLayout>
  );
}
