import axiosClient from "../api/axiosClient";

export async function uploadResume(file, jobDescription) {
  const formData = new FormData();
  formData.append("file", file);
  if (jobDescription && jobDescription.trim()) {
    formData.append("jobDescription", jobDescription.trim());
  }
  const { data } = await axiosClient.post("/student/resume", formData, {
    headers: { "Content-Type": "multipart/form-data" },
    timeout: 30000,
  });
  return data;
}

export async function getResumeVersions() {
  const { data } = await axiosClient.get("/student/resume/versions");
  return data;
}
