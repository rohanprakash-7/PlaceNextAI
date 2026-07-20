import { FiUsers, FiUserCheck, FiFileText, FiAward } from "react-icons/fi";
import AdminPage from "../components/dashboard/AdminPage.jsx";

export default function AdminStudents() {
  return (
    <AdminPage
      navTitle="Students"
      title="Students Management"
      description="Manage all registered students."
      icon={FiUsers}
      emptyIcon={FiUsers}
      emptyTitle="Student records coming soon"
      emptyMessage="The student directory with search, filters, profiles and CRUD operations will be implemented in the next phase. This page is wired and ready for it."
      highlights={[
        { icon: FiUsers, label: "Registered students", value: "—" },
        { icon: FiUserCheck, label: "Placement ready", value: "—" },
        { icon: FiFileText, label: "Resumes uploaded", value: "—" },
        { icon: FiAward, label: "Placed students", value: "—" },
      ]}
    />
  );
}
