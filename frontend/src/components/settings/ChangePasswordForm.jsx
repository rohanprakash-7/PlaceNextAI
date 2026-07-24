import { useState } from "react";
import { FiLock, FiLoader, FiSave } from "react-icons/fi";
import GradientButton from "../ui/GradientButton.jsx";
import { changePassword } from "../../services/authService";

export default function ChangePasswordForm() {
  const [form, setForm] = useState({ currentPassword: "", newPassword: "", confirmPassword: "" });
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState(false);

  const handleChange = (event) => {
    setForm({ ...form, [event.target.name]: event.target.value });
    setError("");
    setSuccess(false);
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    if (form.newPassword !== form.confirmPassword) {
      setError("New password and confirmation don't match");
      return;
    }
    setSaving(true);
    setError("");
    try {
      await changePassword({ currentPassword: form.currentPassword, newPassword: form.newPassword });
      setForm({ currentPassword: "", newPassword: "", confirmPassword: "" });
      setSuccess(true);
    } catch (err) {
      setError(err.friendlyMessage || "Could not change your password");
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="glass-card p-6">
      <div className="flex items-center gap-2">
        <FiLock className="text-primary-400" size={16} />
        <h2 className="font-display text-lg font-semibold text-slate-900 dark:text-white">Change password</h2>
      </div>

      {success && (
        <p className="mt-4 rounded-lg border border-emerald-500/30 bg-emerald-500/10 px-3 py-2 text-xs text-emerald-500">
          Password updated.
        </p>
      )}
      {error && (
        <p className="mt-4 rounded-lg border border-rose-500/30 bg-rose-500/10 px-3 py-2 text-xs text-rose-300">
          {error}
        </p>
      )}

      <form onSubmit={handleSubmit} className="mt-4 space-y-3">
        <input
          type="password"
          name="currentPassword"
          required
          autoComplete="current-password"
          value={form.currentPassword}
          onChange={handleChange}
          placeholder="Current password"
          className="input-glass"
        />
        <input
          type="password"
          name="newPassword"
          required
          minLength={8}
          autoComplete="new-password"
          value={form.newPassword}
          onChange={handleChange}
          placeholder="New password (8+ characters)"
          className="input-glass"
        />
        <input
          type="password"
          name="confirmPassword"
          required
          minLength={8}
          autoComplete="new-password"
          value={form.confirmPassword}
          onChange={handleChange}
          placeholder="Confirm new password"
          className="input-glass"
        />
        <GradientButton type="submit" disabled={saving} className="w-full">
          {saving ? (
            <>
              <FiLoader className="animate-spin" size={16} /> Saving…
            </>
          ) : (
            <>
              <FiSave size={15} /> Update password
            </>
          )}
        </GradientButton>
      </form>
    </div>
  );
}
