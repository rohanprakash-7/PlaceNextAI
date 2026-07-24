import { useCallback, useEffect, useState } from "react";
import { motion } from "framer-motion";
import { FiUser, FiLoader, FiSave } from "react-icons/fi";
import DashboardLayout from "../components/dashboard/DashboardLayout.jsx";
import ErrorState from "../components/ui/ErrorState.jsx";
import GradientButton from "../components/ui/GradientButton.jsx";
import ChangePasswordForm from "../components/settings/ChangePasswordForm.jsx";
import { SkeletonBlock } from "../components/ui/Skeleton.jsx";
import { STUDENT_NAV } from "../constants";
import { useAuth } from "../context/AuthContext.jsx";
import { getStudentProfile, updateStudentProfile } from "../services/studentProfileService";

const EMPTY_FORM = {
  fullName: "",
  phone: "",
  college: "",
  branch: "",
  graduationYear: "",
  cgpa: "",
  skills: "",
};

export default function StudentSettingsPage() {
  const { user } = useAuth();
  const [form, setForm] = useState(EMPTY_FORM);
  const [email, setEmail] = useState("");
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");
  const [saved, setSaved] = useState(false);

  const load = useCallback(async () => {
    setLoading(true);
    setError("");
    try {
      const profile = await getStudentProfile();
      setEmail(profile.email);
      setForm({
        fullName: profile.fullName || "",
        phone: profile.phone || "",
        college: profile.college || "",
        branch: profile.branch || "",
        graduationYear: profile.graduationYear ?? "",
        cgpa: profile.cgpa ?? "",
        skills: profile.skills || "",
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
      await updateStudentProfile({
        ...form,
        graduationYear: form.graduationYear === "" ? null : Number(form.graduationYear),
        cgpa: form.cgpa === "" ? null : Number(form.cgpa),
      });
      setSaved(true);
    } catch (err) {
      setError(err.friendlyMessage || "Could not save your profile");
    } finally {
      setSaving(false);
    }
  };

  return (
    <DashboardLayout navItems={STUDENT_NAV} roleLabel="Student" title="Settings" userName={user?.name || "Student"}>
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
              <FiUser className="text-primary-400" size={16} />
              <h2 className="font-display text-lg font-semibold text-slate-900 dark:text-white">Profile</h2>
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
                placeholder="Full name"
                value={form.fullName}
                onChange={handleChange("fullName")}
                className="input-glass"
              />
              <input
                type="text"
                placeholder="Phone"
                value={form.phone}
                onChange={handleChange("phone")}
                className="input-glass"
              />
              <div className="grid grid-cols-2 gap-3">
                <input
                  type="text"
                  placeholder="College"
                  value={form.college}
                  onChange={handleChange("college")}
                  className="input-glass"
                />
                <input
                  type="text"
                  placeholder="Branch"
                  value={form.branch}
                  onChange={handleChange("branch")}
                  className="input-glass"
                />
              </div>
              <div className="grid grid-cols-2 gap-3">
                <input
                  type="number"
                  placeholder="Graduation year"
                  value={form.graduationYear}
                  onChange={handleChange("graduationYear")}
                  className="input-glass"
                />
                <input
                  type="number"
                  step="0.01"
                  min="0"
                  max="10"
                  placeholder="CGPA"
                  value={form.cgpa}
                  onChange={handleChange("cgpa")}
                  className="input-glass"
                />
              </div>
              <textarea
                rows={3}
                placeholder="Skills (comma separated)"
                value={form.skills}
                onChange={handleChange("skills")}
                className="input-glass resize-none"
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
