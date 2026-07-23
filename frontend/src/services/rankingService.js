import axiosClient from "../api/axiosClient";

export async function getRanking(jobId) {
  const { data } = await axiosClient.get("/recruiter/jobs/" + jobId + "/ranking");
  return data;
}

export async function compareCandidates(jobId, ids) {
  const { data } = await axiosClient.get("/recruiter/candidates/compare", {
    params: { jobId, ids: ids.join(",") },
  });
  return data;
}
