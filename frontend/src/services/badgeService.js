import axiosClient from "../api/axiosClient";

export async function getMyBadges() {
  const { data } = await axiosClient.get("/student/badges");
  return data;
}
