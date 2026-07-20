import { FiActivity, FiServer, FiDatabase, FiCpu } from "react-icons/fi";
import AdminPage from "../components/dashboard/AdminPage.jsx";

export default function AdminSystemHealth() {
  return (
    <AdminPage
      navTitle="System Health"
      title="System Health"
      description="Monitor backend, database and services."
      icon={FiActivity}
      emptyIcon={FiActivity}
      emptyTitle="Live monitoring coming soon"
      emptyMessage="Backend status, database connectivity, AI service health, memory, CPU and uptime charts with auto-refresh will be implemented in the monitoring phase."
      highlights={[
        { icon: FiServer, label: "Backend API", value: "—" },
        { icon: FiDatabase, label: "MySQL Database", value: "—" },
        { icon: FiCpu, label: "AI Service", value: "—" },
        { icon: FiActivity, label: "Server uptime", value: "—" },
      ]}
    />
  );
}
