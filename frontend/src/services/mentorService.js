import axiosClient from "../api/axiosClient";

export async function browseMentors() {
  const { data } = await axiosClient.get("/mentors");
  return data;
}

export async function bookMentorSession(slotId) {
  const { data } = await axiosClient.post("/student/mentor-sessions/book", { slotId });
  return data;
}

export async function getMyMentorSessions() {
  const { data } = await axiosClient.get("/student/mentor-sessions");
  return data;
}

export async function downloadCalendarInvite(slotId) {
  const { data } = await axiosClient.get("/student/mentor-sessions/" + slotId + "/calendar.ics", {
    responseType: "blob",
  });

  const url = window.URL.createObjectURL(data);
  const link = document.createElement("a");
  link.href = url;
  link.download = "mentor-session-" + slotId + ".ics";
  document.body.appendChild(link);
  link.click();
  link.remove();
  window.URL.revokeObjectURL(url);
}

export async function createMentorSlot(payload) {
  const { data } = await axiosClient.post("/alumni/slots", payload);
  return data;
}

export async function getMySlots() {
  const { data } = await axiosClient.get("/alumni/slots");
  return data;
}

export async function getMyAlumniSessions() {
  const { data } = await axiosClient.get("/alumni/mentor-sessions");
  return data;
}

export async function postInterviewExperience(payload) {
  const { data } = await axiosClient.post("/alumni/interview-experiences", payload);
  return data;
}
