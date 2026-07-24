import axiosClient from "../api/axiosClient";

export async function startInterview(targetCompany) {
  const { data } = await axiosClient.post("/student/interviews/start", { targetCompany });
  return data;
}

export async function getInterviewSession(id) {
  const { data } = await axiosClient.get("/student/interviews/" + id);
  return data;
}

export async function submitInterviewAnswer(sessionId, questionId, answerText) {
  const { data } = await axiosClient.post(
    "/student/interviews/" + sessionId + "/questions/" + questionId + "/answer",
    { answerText }
  );
  return data;
}

export async function completeInterview(sessionId) {
  const { data } = await axiosClient.post("/student/interviews/" + sessionId + "/complete");
  return data;
}

export async function getInterviewHistory() {
  const { data } = await axiosClient.get("/student/interviews/history");
  return data;
}
