import axiosClient from "../api/axiosClient";

export async function getReadiness() {
  const { data } = await axiosClient.get("/student/readiness");
  return data;
}

export async function recomputeReadiness() {
  const { data } = await axiosClient.post("/student/readiness/recompute");
  return data;
}

export async function getRecentEvents() {
  const { data } = await axiosClient.get("/student/readiness/events");
  return data;
}
