import axiosClient from "../api/axiosClient";

export async function getSkillGap(company) {
  const query = company ? `?company=${encodeURIComponent(company)}` : "";
  const { data } = await axiosClient.get(`/student/skill-gap${query}`);
  return data;
}

export async function getTargetCompanies() {
  const { data } = await axiosClient.get("/student/skill-gap/companies");
  return data;
}

export async function generateRoadmap(targetCompany) {
  const { data } = await axiosClient.post("/student/roadmap/generate", { targetCompany });
  return data;
}

export async function getActiveRoadmap() {
  const { data } = await axiosClient.get("/student/roadmap");
  return data;
}

export async function completeRoadmapItem(itemId) {
  const { data } = await axiosClient.post(`/student/roadmap/items/${itemId}/complete`);
  return data;
}
