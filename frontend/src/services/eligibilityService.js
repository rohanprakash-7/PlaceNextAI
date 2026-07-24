import axiosClient from "../api/axiosClient";

export async function getEligibilityCompanies() {
  const { data } = await axiosClient.get("/student/eligibility/companies");
  return data;
}

export async function getEligibilityForCompany(company) {
  const { data } = await axiosClient.get("/student/eligibility/company/" + encodeURIComponent(company));
  return data;
}

export async function getEligibilityForJob(jobId) {
  const { data } = await axiosClient.get("/student/eligibility/job/" + jobId);
  return data;
}
