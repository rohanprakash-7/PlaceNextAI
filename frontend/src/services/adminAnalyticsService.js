import axiosClient from "../api/axiosClient";

export async function getOverview() {
  const { data } = await axiosClient.get("/admin/analytics/overview");
  return data;
}

export async function getDepartments() {
  const { data } = await axiosClient.get("/admin/analytics/departments");
  return data;
}

export async function getRecruiterActivity() {
  const { data } = await axiosClient.get("/admin/analytics/recruiters");
  return data;
}

export async function getRiskDistribution() {
  const { data } = await axiosClient.get("/admin/analytics/risk-distribution");
  return data;
}

export async function getColleges() {
  const { data } = await axiosClient.get("/admin/analytics/colleges");
  return data;
}

export async function getStudentAnalytics() {
  const { data } = await axiosClient.get("/admin/analytics/students");
  return data;
}

export async function getHiringTrends() {
  const { data } = await axiosClient.get("/admin/analytics/hiring-trends");
  return data;
}

export async function getResumeStats() {
  const { data } = await axiosClient.get("/admin/analytics/resume-stats");
  return data;
}

export async function getInterviewStats() {
  const { data } = await axiosClient.get("/admin/analytics/interview-stats");
  return data;
}

export async function getSkillAnalytics() {
  const { data } = await axiosClient.get("/admin/analytics/skills");
  return data;
}

export async function getAiPredictionAnalytics() {
  const { data } = await axiosClient.get("/admin/analytics/ai-predictions");
  return data;
}

export async function getPlatformHeatmap(days = 90) {
  const { data } = await axiosClient.get("/admin/analytics/heatmap", { params: { days } });
  return data;
}

export async function downloadReport(format) {
  const { data } = await axiosClient.get("/admin/reports/export", {
    params: { format },
    responseType: "blob",
  });

  const url = window.URL.createObjectURL(data);
  const link = document.createElement("a");
  link.href = url;
  link.download = "placenextai-report." + format;
  document.body.appendChild(link);
  link.click();
  link.remove();
  window.URL.revokeObjectURL(url);
}
