import { useCallback, useEffect, useState } from "react";
import { FiCalendar, FiLoader, FiPlus, FiTrash2 } from "react-icons/fi";
import DashboardLayout from "../components/dashboard/DashboardLayout.jsx";
import EmptyState from "../components/ui/EmptyState.jsx";
import ErrorState from "../components/ui/ErrorState.jsx";
import GradientButton from "../components/ui/GradientButton.jsx";
import { SkeletonBlock } from "../components/ui/Skeleton.jsx";
import { ALUMNI_NAV } from "../constants";
import { useAuth } from "../context/AuthContext.jsx";
import { createMentorSlot, deleteMentorSlot, getMySlots } from "../services/mentorService";

export default function AlumniSlotsPage() {
  const { user } = useAuth();

  const [slots, setSlots] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [creating, setCreating] = useState(false);
  const [deletingId, setDeletingId] = useState(null);
  const [form, setForm] = useState({ startTime: "", endTime: "" });

  const load = useCallback(async () => {
    setLoading(true);
    setError("");
    try {
      setSlots(await getMySlots());
    } catch (err) {
      setError(err.friendlyMessage || "Failed to load your slots");
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
      await createMentorSlot({
        startTime: form.startTime,
        endTime: form.endTime,
      });
      setForm({ startTime: "", endTime: "" });
      await load();
    } catch (err) {
      setError(err.friendlyMessage || "Could not create the slot");
    } finally {
      setCreating(false);
    }
  };

  const handleDelete = async (slotId) => {
    setDeletingId(slotId);
    setError("");
    try {
      await deleteMentorSlot(slotId);
      await load();
    } catch (err) {
      setError(err.friendlyMessage || "Could not delete the slot");
    } finally {
      setDeletingId(null);
    }
  };

  return (
    <DashboardLayout navItems={ALUMNI_NAV} roleLabel="Alumni" title="My Slots" userName={user?.name || "User"}>
      <div className="glass-card p-6">
        <div className="flex items-center gap-2">
          <FiCalendar className="text-primary-400" size={16} />
          <h2 className="font-display text-lg font-semibold text-slate-900 dark:text-white">Add availability</h2>
        </div>
        <form onSubmit={handleCreate} className="mt-4 grid gap-3 sm:grid-cols-3">
          <input
            type="datetime-local"
            required
            value={form.startTime}
            onChange={(event) => setForm({ ...form, startTime: event.target.value })}
            className="input-glass"
          />
          <input
            type="datetime-local"
            required
            value={form.endTime}
            onChange={(event) => setForm({ ...form, endTime: event.target.value })}
            className="input-glass"
          />
          <GradientButton type="submit" disabled={creating} className="justify-center">
            {creating ? (
              <>
                <FiLoader className="animate-spin" size={16} /> Adding…
              </>
            ) : (
              <>
                <FiPlus size={16} /> Add slot
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

        {!loading && slots.length === 0 && (
          <div className="glass-card">
            <EmptyState
              icon={FiCalendar}
              title="No slots yet"
              message="Add an availability window above so students can book a mentor session with you."
            />
          </div>
        )}

        {!loading && slots.length > 0 && (
          <div className="glass-card overflow-hidden">
            <div className="overflow-x-auto">
              <table className="w-full min-w-[480px] text-left text-sm">
                <thead>
                  <tr className="border-b border-slate-200 dark:border-white/5 text-xs uppercase tracking-wider text-slate-500">
                    <th className="px-6 py-3.5 font-medium">Start</th>
                    <th className="px-6 py-3.5 font-medium">End</th>
                    <th className="px-6 py-3.5 font-medium">Status</th>
                    <th className="px-6 py-3.5 font-medium"></th>
                  </tr>
                </thead>
                <tbody>
                  {slots.map((slot) => (
                    <tr key={slot.id} className="border-b border-slate-200 dark:border-white/5 last:border-0">
                      <td className="px-6 py-3.5 text-slate-700 dark:text-slate-300">{new Date(slot.startTime).toLocaleString()}</td>
                      <td className="px-6 py-3.5 text-slate-700 dark:text-slate-300">{new Date(slot.endTime).toLocaleString()}</td>
                      <td className="px-6 py-3.5">
                        <span
                          className={
                            "rounded-full px-2.5 py-1 text-xs font-semibold " +
                            (slot.booked ? "bg-emerald-500/10 text-emerald-400" : "bg-white/5 text-slate-500 dark:text-slate-400")
                          }
                        >
                          {slot.booked ? "Booked" : "Open"}
                        </span>
                      </td>
                      <td className="px-6 py-3.5 text-right">
                        {!slot.booked && (
                          <button
                            type="button"
                            onClick={() => handleDelete(slot.id)}
                            disabled={deletingId === slot.id}
                            aria-label="Delete slot"
                            className="text-slate-500 transition-colors hover:text-rose-400 disabled:opacity-50"
                          >
                            {deletingId === slot.id ? (
                              <FiLoader className="animate-spin" size={14} />
                            ) : (
                              <FiTrash2 size={14} />
                            )}
                          </button>
                        )}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        )}
      </div>
    </DashboardLayout>
  );
}
