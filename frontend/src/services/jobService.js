import axiosClient from "../api/axiosClient";

export async function getJobsForRecruiter() {
  const { data } = await axiosClient.get("/recruiter/jobs");
  return data;
}
