import axiosClient from "../api/axiosClient";

export async function getStudentProfile() {
  const { data } = await axiosClient.get("/student/profile");
  return data;
}

export async function updateStudentProfile(payload) {
  const { data } = await axiosClient.put("/student/profile", payload);
  return data;
}
