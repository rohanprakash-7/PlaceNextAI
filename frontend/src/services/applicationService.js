import axiosClient from "../api/axiosClient";

export async function getRecruiterApplications() {
  const { data } = await axiosClient.get("/application/recruiter");
  return data;
}

export async function getStudentApplications() {
  const { data } = await axiosClient.get("/application/student");
  return data;
}

export async function updateApplicationStatus(applicationId, status) {
  const { data } = await axiosClient.put(`/application/${applicationId}/status`, { status });
  return data;
}

export async function getApplicationTimeline(applicationId) {
  const { data } = await axiosClient.get(`/application/${applicationId}/timeline`);
  return data;
}

export async function submitFeedback(applicationId, payload) {
  const { data } = await axiosClient.post(`/application/${applicationId}/feedback`, payload);
  return data;
}

export async function getApplicationFeedback(applicationId) {
  const { data } = await axiosClient.get(`/application/${applicationId}/feedback`);
  return data;
}

export async function getFeedbackSummary() {
  const { data } = await axiosClient.get("/student/feedback-summary");
  return data;
}
