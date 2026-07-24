import axiosClient from "../api/axiosClient";

export async function getRecruiterProfile() {
  const { data } = await axiosClient.get("/recruiter/profile");
  return data;
}

export async function updateRecruiterProfile(payload) {
  const { data } = await axiosClient.put("/recruiter/profile", payload);
  return data;
}
