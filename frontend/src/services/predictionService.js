import axiosClient from "../api/axiosClient";

export async function getPrediction() {
  const { data } = await axiosClient.get("/student/prediction");
  return data;
}

export async function recomputePrediction() {
  const { data } = await axiosClient.post("/student/prediction/recompute");
  return data;
}
