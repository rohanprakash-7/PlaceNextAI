import axiosClient from "../api/axiosClient";

export async function getFunnel() {
  const { data } = await axiosClient.get("/recruiter/analytics/funnel");
  return data;
}

export async function getSkillDistribution() {
  const { data } = await axiosClient.get("/recruiter/analytics/skills");
  return data;
}

export async function getDepartmentBreakdown() {
  const { data } = await axiosClient.get("/recruiter/analytics/departments");
  return data;
}
