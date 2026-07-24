import axiosClient from "../api/axiosClient";

export async function getMyGamificationSummary() {
  const { data } = await axiosClient.get("/student/gamification");
  return data;
}

export async function getLeaderboard() {
  const { data } = await axiosClient.get("/student/leaderboard");
  return data;
}
