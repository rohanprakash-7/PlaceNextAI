import axiosClient from "../api/axiosClient";

export async function getJobsForRecruiter() {
  const { data } = await axiosClient.get("/recruiter/jobs");
  return data;
}

export async function createJob(payload) {
  const { data } = await axiosClient.post("/recruiter/jobs", payload);
  return data;
}
