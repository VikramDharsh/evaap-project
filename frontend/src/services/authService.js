import axios from "axios";

/** Axios instance scoped to the auth API */
const apiClient = axios.create({
  baseURL: "http://localhost:8080/api/v1",
  headers: {
    "Content-Type": "application/json",
  },
});

/**
 * Registers a new user.
 * @param {{ fullName: string, email: string, password: string, role: string }} data
 */
export const registerUser = async (data) => {
  const response = await apiClient.post("/auth/register", data);
  return response.data;
};
