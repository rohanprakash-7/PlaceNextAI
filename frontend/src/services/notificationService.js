import axiosClient from "../api/axiosClient";

export async function getNotifications() {
  const { data } = await axiosClient.get("/notifications");
  return data;
}

export async function getUnreadCount() {
  const { data } = await axiosClient.get("/notifications/unread-count");
  return data.count;
}

export async function markNotificationRead(id) {
  await axiosClient.patch("/notifications/" + id + "/read");
}

export async function markAllNotificationsRead() {
  await axiosClient.patch("/notifications/read-all");
}
