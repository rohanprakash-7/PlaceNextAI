import { useCallback, useEffect, useState } from "react";
import { motion } from "framer-motion";
import { FiSave, FiLoader, FiStar } from "react-icons/fi";
import DashboardLayout from "../components/dashboard/DashboardLayout.jsx";
import ErrorState from "../components/ui/ErrorState.jsx";
import GradientButton from "../components/ui/GradientButton.jsx";
import ChangePasswordForm from "../components/settings/ChangePasswordForm.jsx";
import { SkeletonBlock } from "../components/ui/Skeleton.jsx";
import { ALUMNI_NAV } from "../constants";
import { useAuth } from "../context/AuthContext.jsx";
import { getMyAlumniProfile, updateAlumniProfile } from "../services/mentorService";

const EMPTY_FORM = {
  currentCompany: "",
  designation: "",
  graduationYear: "",
  expertise: "",
  bio: "",
  linkedinUrl: "",
  profileImageUrl: "",
  yearsOfExperience: "",
};

export default function AlumniProfilePage() {
  const { user } = useAuth();
  const [form, setForm] = useState(EMPTY_FORM);
  const [rating, setRating] = useState({ averageRating: null, reviewCount: 0 });
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");
  const [saved, setSaved] = useState(false);

  const load = useCallback(async () => {
    setLoading(true);
    setError("");
    try {
      const profile = await getMyAlumniProfile();
      setForm({
        currentCompany: profile.currentCompany || "",
        designation: profile.designation || "",
        graduationYear: profile.graduationYear || "",
        expertise: profile.expertise || "",
        bio: profile.bio || "",
        linkedinUrl: profile.linkedinUrl || "",
        profileImageUrl: profile.profileImageUrl || "",
        yearsOfExperience: profile.yearsOfExperience ?? "",
      });
      setRating({ averageRating: profile.averageRating, reviewCount: profile.reviewCount });
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
      await updateAlumniProfile({
        ...form,
        graduationYear: form.graduationYear === "" ? null : Number(form.graduationYear),
        yearsOfExperience: form.yearsOfExperience === "" ? null : Number(form.yearsOfExperience),
      });
      setSaved(true);
    } catch (err) {
      setError(err.friendlyMessage || "Could not save your profile");
    } finally {
      setSaving(false);
    }
  };

  return (
    <DashboardLayout navItems={ALUMNI_NAV} roleLabel="Alumni" title="My Profile" userName={user?.name || "User"}>
      {loading && <SkeletonBlock className="h-96" />}

      {!loading && error && (
        <div className="glass-card mb-5">
          <ErrorState message={error} onRetry={load} />
        </div>
      )}

      {!loading && (
        <div className="grid gap-5 lg:grid-cols-2">
        <motion.div initial={{ opacity: 0, y: 16 }} animate={{ opacity: 1, y: 0 }} className="glass-card p-6">
          <div className="flex items-center gap-2 text-sm text-slate-500 dark:text-slate-400">
            <FiStar className="text-amber-400" size={15} />
            {rating.averageRating ? rating.averageRating.toFixed(1) + " average rating" : "No ratings yet"}
            {rating.reviewCount ? " · " + rating.reviewCount + " review" + (rating.reviewCount === 1 ? "" : "s") : ""}
          </div>

          {saved && (
            <p className="mt-4 rounded-lg border border-emerald-500/30 bg-emerald-500/10 px-3 py-2 text-xs text-emerald-300">
              Profile updated.
            </p>
          )}

          <form onSubmit={handleSubmit} className="mt-4 grid gap-4 sm:grid-cols-2">
            <input
              type="text"
              placeholder="Current company"
              value={form.currentCompany}
              onChange={handleChange("currentCompany")}
              className="input-glass"
            />
            <input
              type="text"
              placeholder="Designation"
              value={form.designation}
              onChange={handleChange("designation")}
              className="input-glass"
            />
            <input
              type="number"
              placeholder="Graduation year"
              value={form.graduationYear}
              onChange={handleChange("graduationYear")}
              className="input-glass"
            />
            <input
              type="number"
              min="0"
              placeholder="Years of experience"
              value={form.yearsOfExperience}
              onChange={handleChange("yearsOfExperience")}
              className="input-glass"
            />
            <input
              type="text"
              placeholder="Expertise (comma separated)"
              value={form.expertise}
              onChange={handleChange("expertise")}
              className="input-glass sm:col-span-2"
            />
            <input
              type="url"
              placeholder="LinkedIn URL"
              value={form.linkedinUrl}
              onChange={handleChange("linkedinUrl")}
              className="input-glass sm:col-span-2"
            />
            <input
              type="url"
              placeholder="Profile photo URL"
              value={form.profileImageUrl}
              onChange={handleChange("profileImageUrl")}
              className="input-glass sm:col-span-2"
            />
            <textarea
              rows={4}
              placeholder="Bio"
              value={form.bio}
              onChange={handleChange("bio")}
              className="input-glass resize-none sm:col-span-2"
            />
            <GradientButton type="submit" disabled={saving} className="sm:col-span-2">
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
