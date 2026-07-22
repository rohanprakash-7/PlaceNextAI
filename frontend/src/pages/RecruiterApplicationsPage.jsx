import { useCallback, useEffect, useState } from "react";
import { FiUsers, FiMessageSquare } from "react-icons/fi";
import DashboardLayout from "../components/dashboard/DashboardLayout.jsx";
import StatusBadge, { humanizeStatus } from "../components/applications/StatusBadge.jsx";
import FeedbackFormModal from "../components/applications/FeedbackFormModal.jsx";
import EmptyState from "../components/ui/EmptyState.jsx";
import ErrorState from "../components/ui/ErrorState.jsx";
import { TableSkeleton } from "../components/ui/Skeleton.jsx";
import { RECRUITER_NAV } from "../constants";
import { useAuth } from "../context/AuthContext.jsx";
import { useToast } from "../context/ToastContext.jsx";
import {
  getRecruiterApplications,
  updateApplicationStatus,
  submitFeedback,
} from "../services/applicationService";

const STATUS_OPTIONS = [
  "APPLIED", "SHORTLISTED", "ASSESSMENT", "TECHNICAL_INTERVIEW",
  "HR_INTERVIEW", "OFFERED", "REJECTED", "HIRED",
];

export default function RecruiterApplicationsPage() {
  const { user } = useAuth();
  const toast = useToast();

  const [applications, setApplications] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [updatingId, setUpdatingId] = useState(null);
  const [feedbackTarget, setFeedbackTarget] = useState(null);
  const [submitting, setSubmitting] = useState(false);

  const load = useCallback(async () => {
    setLoading(true);
    setError("");
    try {
      const data = await getRecruiterApplications();
      setApplications(data);
    } catch (err) {
      setError(err.friendlyMessage || "Failed to load applications");
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    load();
  }, [load]);

  const handleStatusChange = async (applicationId, status) => {
    setUpdatingId(applicationId);
    try {
      const updated = await updateApplicationStatus(applicationId, status);
      setApplications((current) =>
        current.map((application) => (application.id === applicationId ? updated : application))
      );
      toast.success("Status updated — the candidate's dashboard reflects this instantly");
    } catch (err) {
      toast.error(err.friendlyMessage || "Could not update status");
    } finally {
      setUpdatingId(null);
    }
  };

  const handleFeedbackSubmit = async (payload) => {
    setSubmitting(true);
    try {
      await submitFeedback(feedbackTarget.id, payload);
      toast.success("Feedback submitted — the candidate's readiness score has been updated");
      setFeedbackTarget(null);
    } catch (err) {
      toast.error(err.friendlyMessage || "Could not submit feedback");
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <DashboardLayout
      navItems={RECRUITER_NAV}
      roleLabel="Recruiter"
      title="Candidates"
      userName={user?.name || "Recruiter"}
    >
      <div className="glass-card overflow-hidden">
        {loading && <TableSkeleton rows={6} columns={5} />}

        {!loading && error && <ErrorState message={error} onRetry={load} />}

        {!loading && !error && applications.length === 0 && (
          <EmptyState
            icon={FiUsers}
            title="No applications yet"
            message="Once students apply to your job postings, they'll appear here for review and feedback."
          />
        )}

        {!loading && !error && applications.length > 0 && (
          <div className="overflow-x-auto">
            <table className="w-full min-w-[760px] text-left text-sm">
              <thead>
                <tr className="border-b border-white/5 text-xs uppercase tracking-wider text-slate-500">
                  <th className="px-5 py-3.5 font-medium">Candidate</th>
                  <th className="px-5 py-3.5 font-medium">Role</th>
                  <th className="px-5 py-3.5 font-medium">Applied</th>
                  <th className="px-5 py-3.5 font-medium">Status</th>
                  <th className="px-5 py-3.5 font-medium">Actions</th>
                </tr>
              </thead>
              <tbody>
                {applications.map((application) => (
                  <tr
                    key={application.id}
                    className="border-b border-white/5 transition-colors last:border-0 hover:bg-white/[0.03]"
                  >
                    <td className="px-5 py-3.5">
                      <p className="font-medium text-white">{application.studentName}</p>
                      <p className="text-xs text-slate-500">{application.studentEmail}</p>
                    </td>
                    <td className="px-5 py-3.5 text-slate-400">{application.jobTitle}</td>
                    <td className="px-5 py-3.5 text-xs text-slate-500">
                      {new Date(application.appliedDate).toLocaleDateString()}
                    </td>
                    <td className="px-5 py-3.5">
                      <select
                        value={application.status}
                        disabled={updatingId === application.id}
                        onChange={(event) => handleStatusChange(application.id, event.target.value)}
                        className="glass rounded-lg bg-night-800 px-2.5 py-1.5 text-xs text-slate-200 outline-none"
                      >
                        {STATUS_OPTIONS.map((status) => (
                          <option key={status} value={status} className="bg-night-800">
                            {humanizeStatus(status)}
                          </option>
                        ))}
                      </select>
                    </td>
                    <td className="px-5 py-3.5">
                      <button
                        type="button"
                        onClick={() => setFeedbackTarget(application)}
                        className="glass inline-flex items-center gap-1.5 rounded-lg px-3 py-1.5 text-xs font-medium text-slate-300 transition-colors hover:text-primary-400"
                      >
                        <FiMessageSquare size={13} /> Feedback
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {feedbackTarget && (
        <FeedbackFormModal
          open={Boolean(feedbackTarget)}
          onClose={() => setFeedbackTarget(null)}
          onSubmit={handleFeedbackSubmit}
          applicantName={feedbackTarget.studentName}
          submitting={submitting}
        />
      )}
    </DashboardLayout>
  );
}
