import axiosClient from "../api/axiosClient";

export async function getOverview() {
  const { data } = await axiosClient.get("/admin/analytics/overview");
  return data;
}

export async function getDepartments() {
  const { data } = await axiosClient.get("/admin/analytics/departments");
  return data;
}

export async function getRecruiterActivity() {
  const { data } = await axiosClient.get("/admin/analytics/recruiters");
  return data;
}

export async function getRiskDistribution() {
  const { data } = await axiosClient.get("/admin/analytics/risk-distribution");
  return data;
}

export async function downloadReport(format) {
  const { data } = await axiosClient.get("/admin/reports/export", {
    params: { format },
    responseType: "blob",
  });

  const url = window.URL.createObjectURL(data);
  const link = document.createElement("a");
  link.href = url;
  link.download = "placenextai-report." + format;
  document.body.appendChild(link);
  link.click();
  link.remove();
  window.URL.revokeObjectURL(url);
}
