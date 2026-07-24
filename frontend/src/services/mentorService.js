import axiosClient from "../api/axiosClient";

export async function browseMentors({ search, company } = {}) {
  const { data } = await axiosClient.get("/mentors", { params: { search, company } });
  return data;
}

export async function getMentorCompanies() {
  const { data } = await axiosClient.get("/mentors/companies");
  return data;
}

export async function getMentorProfile(alumniId) {
  const { data } = await axiosClient.get("/mentors/" + alumniId);
  return data;
}

export async function getMentorReviews(alumniId) {
  const { data } = await axiosClient.get("/mentors/" + alumniId + "/reviews");
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

export async function deleteMentorSlot(slotId) {
  await axiosClient.delete("/alumni/slots/" + slotId);
}

export async function getMyAlumniProfile() {
  const { data } = await axiosClient.get("/alumni/profile");
  return data;
}

export async function updateAlumniProfile(payload) {
  const { data } = await axiosClient.put("/alumni/profile", payload);
  return data;
}

// ---------- Mentor requests (ask / accept / reject) ----------

export async function sendMentorRequest(payload) {
  const { data } = await axiosClient.post("/student/mentor-requests", payload);
  return data;
}

export async function getMyMentorRequests() {
  const { data } = await axiosClient.get("/student/mentor-requests");
  return data;
}

export async function getIncomingMentorRequests() {
  const { data } = await axiosClient.get("/alumni/mentor-requests");
  return data;
}

export async function acceptMentorRequest(requestId) {
  const { data } = await axiosClient.patch("/alumni/mentor-requests/" + requestId + "/accept");
  return data;
}

export async function rejectMentorRequest(requestId) {
  const { data } = await axiosClient.patch("/alumni/mentor-requests/" + requestId + "/reject");
  return data;
}

export async function getMentorRequest(requestId) {
  const { data } = await axiosClient.get("/mentor-requests/" + requestId);
  return data;
}

// ---------- Messaging ----------

export async function getMentorMessages(requestId) {
  const { data } = await axiosClient.get("/mentor-requests/" + requestId + "/messages");
  return data;
}

export async function sendMentorMessage(requestId, content) {
  const { data } = await axiosClient.post("/mentor-requests/" + requestId + "/messages", { content });
  return data;
}

// ---------- Ratings & reviews ----------

export async function submitMentorReview(payload) {
  const { data } = await axiosClient.post("/student/mentor-reviews", payload);
  return data;
}

// ---------- Bookmarks ----------

export async function toggleMentorBookmark(alumniId) {
  const { data } = await axiosClient.post("/student/bookmarks/" + alumniId);
  return data;
}

export async function getMentorBookmarks() {
  const { data } = await axiosClient.get("/student/bookmarks");
  return data;
}

// ---------- Success stories ----------

export async function getSuccessStories({ search, company } = {}) {
  const { data } = await axiosClient.get("/success-stories", { params: { search, company } });
  return data;
}

export async function getSuccessStoryCompanies() {
  const { data } = await axiosClient.get("/success-stories/companies");
  return data;
}
