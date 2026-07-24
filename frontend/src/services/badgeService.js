import axiosClient from "../api/axiosClient";

export async function getMyBadges() {
  const { data } = await axiosClient.get("/student/badges");
  return data;
}

export async function getMyRecruiterBadges() {
  const { data } = await axiosClient.get("/recruiter/badges");
  return data;
}

export async function downloadBadgeCertificate(code) {
  const { data } = await axiosClient.get("/student/badges/" + code + "/certificate", {
    responseType: "blob",
  });

  const url = window.URL.createObjectURL(data);
  const link = document.createElement("a");
  link.href = url;
  link.download = code.toLowerCase() + "-certificate.pdf";
  document.body.appendChild(link);
  link.click();
  link.remove();
  window.URL.revokeObjectURL(url);
}
