import { FiCpu, FiFileText, FiMic, FiTarget } from "react-icons/fi";
import AdminPage from "../components/dashboard/AdminPage.jsx";

export default function AdminAIServices() {
  return (
    <AdminPage
      navTitle="AI Services"
      title="AI Services"
      description="Monitor AI models and services."
      icon={FiCpu}
      emptyIcon={FiCpu}
      emptyTitle="AI service monitoring coming soon"
      emptyMessage="Status, response times, usage counts and health indicators for the Resume Analyzer, Mock Interview agent, Job Recommendation engine and Skill Gap Analysis will live here once the FastAPI service is integrated."
      highlights={[
        { icon: FiFileText, label: "Resume Analyzer", value: "Offline" },
        { icon: FiMic, label: "Mock Interview", value: "Offline" },
        { icon: FiTarget, label: "Job Recommendation", value: "Offline" },
        { icon: FiCpu, label: "Skill Gap Analysis", value: "Offline" },
      ]}
    />
  );
}
