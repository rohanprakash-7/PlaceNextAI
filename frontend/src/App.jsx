import { useEffect } from "react";
import { Routes, Route, useLocation } from "react-router-dom";
import { AnimatePresence } from "framer-motion";

import ProtectedRoute from "./routes/ProtectedRoute.jsx";
import LandingPage from "./pages/LandingPage.jsx";
import LoginPage from "./pages/LoginPage.jsx";
import RegisterPage from "./pages/RegisterPage.jsx";
import StudentDashboard from "./pages/StudentDashboard.jsx";
import StudentResumePage from "./pages/StudentResumePage.jsx";
import StudentRoadmapPage from "./pages/StudentRoadmapPage.jsx";
import StudentPredictionPage from "./pages/StudentPredictionPage.jsx";
import StudentMentorsPage from "./pages/StudentMentorsPage.jsx";
import StudentApplicationsPage from "./pages/StudentApplicationsPage.jsx";
import RecruiterApplicationsPage from "./pages/RecruiterApplicationsPage.jsx";
import RecruiterJobPostingsPage from "./pages/RecruiterJobPostingsPage.jsx";
import CandidateRankingPage from "./pages/CandidateRankingPage.jsx";
import RecruiterAnalyticsPage from "./pages/RecruiterAnalyticsPage.jsx";
import RecruiterDashboard from "./pages/RecruiterDashboard.jsx";
import AdminDashboard from "./pages/AdminDashboard.jsx";
import AdminStudents from "./pages/AdminStudents.jsx";
import AdminRecruiters from "./pages/AdminRecruiters.jsx";
import AdminAIServices from "./pages/AdminAIServices.jsx";
import AdminAnalyticsDashboard from "./pages/AdminAnalyticsDashboard.jsx";
import AdminSystemHealth from "./pages/AdminSystemHealth.jsx";
import AdminSettings from "./pages/AdminSettings.jsx";
import AlumniDashboard from "./pages/AlumniDashboard.jsx";
import AlumniSlotsPage from "./pages/AlumniSlotsPage.jsx";
import AlumniMentorRequestsPage from "./pages/AlumniMentorRequestsPage.jsx";
import AlumniProfilePage from "./pages/AlumniProfilePage.jsx";
import MentorProfilePage from "./pages/MentorProfilePage.jsx";
import StudentMentorRequestsPage from "./pages/StudentMentorRequestsPage.jsx";
import MentorConversationPage from "./pages/MentorConversationPage.jsx";
import SuccessStoriesPage from "./pages/SuccessStoriesPage.jsx";
import AchievementsPage from "./pages/AchievementsPage.jsx";
import LeaderboardPage from "./pages/LeaderboardPage.jsx";
import EligibilityCheckerPage from "./pages/EligibilityCheckerPage.jsx";
import NotificationHistoryPage from "./pages/NotificationHistoryPage.jsx";
import MockInterviewPage from "./pages/MockInterviewPage.jsx";
import InterviewSessionPage from "./pages/InterviewSessionPage.jsx";
import StudentSettingsPage from "./pages/StudentSettingsPage.jsx";
import RecruiterSettingsPage from "./pages/RecruiterSettingsPage.jsx";
import NotFoundPage from "./pages/NotFoundPage.jsx";

function ScrollToTop() {
  const { pathname } = useLocation();

  useEffect(() => {
    window.scrollTo({ top: 0, behavior: "instant" });
  }, [pathname]);

  return null;
}

export default function App() {
  const location = useLocation();

  return (
    <div className="min-h-screen bg-slate-50 dark:bg-night-950">
      <ScrollToTop />
      <AnimatePresence mode="wait">
        <Routes location={location} key={location.pathname}>
          <Route path="/" element={<LandingPage />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />
          <Route
            path="/dashboard/student"
            element={
              <ProtectedRoute allowedRoles={["ROLE_STUDENT"]}>
                <StudentDashboard />
              </ProtectedRoute>
            }
          />
          <Route
            path="/dashboard/student/resume"
            element={
              <ProtectedRoute allowedRoles={["ROLE_STUDENT"]}>
                <StudentResumePage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/dashboard/student/roadmap"
            element={
              <ProtectedRoute allowedRoles={["ROLE_STUDENT"]}>
                <StudentRoadmapPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/dashboard/student/applications"
            element={
              <ProtectedRoute allowedRoles={["ROLE_STUDENT"]}>
                <StudentApplicationsPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/dashboard/student/prediction"
            element={
              <ProtectedRoute allowedRoles={["ROLE_STUDENT"]}>
                <StudentPredictionPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/dashboard/student/mentors"
            element={
              <ProtectedRoute allowedRoles={["ROLE_STUDENT"]}>
                <StudentMentorsPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/dashboard/student/mentors/requests"
            element={
              <ProtectedRoute allowedRoles={["ROLE_STUDENT"]}>
                <StudentMentorRequestsPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/dashboard/student/mentors/requests/:id/messages"
            element={
              <ProtectedRoute allowedRoles={["ROLE_STUDENT"]}>
                <MentorConversationPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/dashboard/student/mentors/:alumniId"
            element={
              <ProtectedRoute allowedRoles={["ROLE_STUDENT"]}>
                <MentorProfilePage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/dashboard/student/success-stories"
            element={
              <ProtectedRoute allowedRoles={["ROLE_STUDENT"]}>
                <SuccessStoriesPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/dashboard/student/achievements"
            element={
              <ProtectedRoute allowedRoles={["ROLE_STUDENT"]}>
                <AchievementsPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/dashboard/student/leaderboard"
            element={
              <ProtectedRoute allowedRoles={["ROLE_STUDENT"]}>
                <LeaderboardPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/dashboard/student/eligibility"
            element={
              <ProtectedRoute allowedRoles={["ROLE_STUDENT"]}>
                <EligibilityCheckerPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/dashboard/student/interviews"
            element={
              <ProtectedRoute allowedRoles={["ROLE_STUDENT"]}>
                <MockInterviewPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/dashboard/student/interviews/:sessionId"
            element={
              <ProtectedRoute allowedRoles={["ROLE_STUDENT"]}>
                <InterviewSessionPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/dashboard/student/settings"
            element={
              <ProtectedRoute allowedRoles={["ROLE_STUDENT"]}>
                <StudentSettingsPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/dashboard/student/notifications"
            element={
              <ProtectedRoute allowedRoles={["ROLE_STUDENT"]}>
                <NotificationHistoryPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/dashboard/recruiter"
            element={
              <ProtectedRoute allowedRoles={["ROLE_RECRUITER"]}>
                <RecruiterDashboard />
              </ProtectedRoute>
            }
          />
          <Route
            path="/dashboard/recruiter/jobs"
            element={
              <ProtectedRoute allowedRoles={["ROLE_RECRUITER"]}>
                <RecruiterJobPostingsPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/dashboard/recruiter/applications"
            element={
              <ProtectedRoute allowedRoles={["ROLE_RECRUITER"]}>
                <RecruiterApplicationsPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/dashboard/recruiter/ranking"
            element={
              <ProtectedRoute allowedRoles={["ROLE_RECRUITER"]}>
                <CandidateRankingPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/dashboard/recruiter/analytics"
            element={
              <ProtectedRoute allowedRoles={["ROLE_RECRUITER"]}>
                <RecruiterAnalyticsPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/dashboard/recruiter/notifications"
            element={
              <ProtectedRoute allowedRoles={["ROLE_RECRUITER"]}>
                <NotificationHistoryPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/dashboard/recruiter/settings"
            element={
              <ProtectedRoute allowedRoles={["ROLE_RECRUITER"]}>
                <RecruiterSettingsPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/dashboard/admin"
            element={
              <ProtectedRoute allowedRoles={["ROLE_ADMIN"]}>
                <AdminDashboard />
              </ProtectedRoute>
            }
          />
          <Route
            path="/dashboard/admin/students"
            element={
              <ProtectedRoute allowedRoles={["ROLE_ADMIN"]}>
                <AdminStudents />
              </ProtectedRoute>
            }
          />
          <Route
            path="/dashboard/admin/recruiters"
            element={
              <ProtectedRoute allowedRoles={["ROLE_ADMIN"]}>
                <AdminRecruiters />
              </ProtectedRoute>
            }
          />
          <Route
            path="/dashboard/admin/ai-services"
            element={
              <ProtectedRoute allowedRoles={["ROLE_ADMIN"]}>
                <AdminAIServices />
              </ProtectedRoute>
            }
          />
          <Route
            path="/dashboard/admin/analytics"
            element={
              <ProtectedRoute allowedRoles={["ROLE_ADMIN"]}>
                <AdminAnalyticsDashboard />
              </ProtectedRoute>
            }
          />
          <Route
            path="/dashboard/admin/system-health"
            element={
              <ProtectedRoute allowedRoles={["ROLE_ADMIN"]}>
                <AdminSystemHealth />
              </ProtectedRoute>
            }
          />
          <Route
            path="/dashboard/admin/settings"
            element={
              <ProtectedRoute allowedRoles={["ROLE_ADMIN"]}>
                <AdminSettings />
              </ProtectedRoute>
            }
          />
          <Route
            path="/dashboard/admin/notifications"
            element={
              <ProtectedRoute allowedRoles={["ROLE_ADMIN"]}>
                <NotificationHistoryPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/dashboard/alumni"
            element={
              <ProtectedRoute allowedRoles={["ROLE_ALUMNI"]}>
                <AlumniDashboard />
              </ProtectedRoute>
            }
          />
          <Route
            path="/dashboard/alumni/slots"
            element={
              <ProtectedRoute allowedRoles={["ROLE_ALUMNI"]}>
                <AlumniSlotsPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/dashboard/alumni/requests"
            element={
              <ProtectedRoute allowedRoles={["ROLE_ALUMNI"]}>
                <AlumniMentorRequestsPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/dashboard/alumni/requests/:id/messages"
            element={
              <ProtectedRoute allowedRoles={["ROLE_ALUMNI"]}>
                <MentorConversationPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/dashboard/alumni/success-stories"
            element={
              <ProtectedRoute allowedRoles={["ROLE_ALUMNI"]}>
                <SuccessStoriesPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/dashboard/alumni/profile"
            element={
              <ProtectedRoute allowedRoles={["ROLE_ALUMNI"]}>
                <AlumniProfilePage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/dashboard/alumni/notifications"
            element={
              <ProtectedRoute allowedRoles={["ROLE_ALUMNI"]}>
                <NotificationHistoryPage />
              </ProtectedRoute>
            }
          />
          <Route path="*" element={<NotFoundPage />} />
        </Routes>
      </AnimatePresence>
    </div>
  );
}
