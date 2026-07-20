import { FiBriefcase, FiUsers, FiTrendingUp, FiCheckCircle } from "react-icons/fi";
import AdminPage from "../components/dashboard/AdminPage.jsx";

export default function AdminRecruiters() {
  return (
    <AdminPage
      navTitle="Recruiters"
      title="Recruiters Management"
      description="Manage recruiter accounts."
      icon={FiBriefcase}
      emptyIcon={FiBriefcase}
      emptyTitle="Recruiter directory coming soon"
      emptyMessage="Recruiter profiles, company associations, posted jobs and hiring statistics will appear here once the recruiters module is implemented."
      highlights={[
        { icon: FiBriefcase, label: "Recruiter accounts", value: "—" },
        { icon: FiUsers, label: "Companies onboarded", value: "—" },
        { icon: FiTrendingUp, label: "Active job postings", value: "—" },
        { icon: FiCheckCircle, label: "Offers released", value: "—" },
      ]}
    />
  );
}
