import axiosClient from "../api/axiosClient";

export async function login(credentials) {
  const { data } = await axiosClient.post("/auth/login", credentials);
  return data;
}

export async function registerStudent(payload) {
  const { data } = await axiosClient.post("/auth/student/register", payload);
  return data;
}

export async function registerRecruiter(payload) {
  const { data } = await axiosClient.post("/auth/recruiter/register", payload);
  return data;
}

export async function registerAlumni(payload) {
  const { data } = await axiosClient.post("/auth/alumni/register", payload);
  return data;
}

export async function getMe() {
  const { data } = await axiosClient.get("/auth/me");
  return data;
}

export async function changePassword(payload) {
  await axiosClient.patch("/auth/change-password", payload);
}
