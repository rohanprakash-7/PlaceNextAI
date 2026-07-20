import { FiSettings } from "react-icons/fi";
import AdminPage from "../components/dashboard/AdminPage.jsx";

export default function AdminSettings() {
  return (
    <AdminPage
      navTitle="Settings"
      title="Platform Settings"
      description="Configure application settings."
      icon={FiSettings}
      emptyIcon={FiSettings}
      emptyTitle="Settings panel coming soon"
      emptyMessage="General settings, email configuration, JWT policy, password rules, theme, notifications and maintenance mode will be configurable here in the settings phase."
    />
  );
}
