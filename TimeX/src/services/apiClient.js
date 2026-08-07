import {
  AUTH_API_URL,
  BUSINESS_API_URL,
  TRANSACTION_API_URL,
} from "../config/api";
import { readApiError } from "../utils/apiError";

async function request(baseUrl, path, options = {}) {
  const { body, headers, ...requestOptions } = options;
  const configuration = {
    ...requestOptions,
    credentials: "include",
    headers: { ...headers },
  };

  // Business forms send JavaScript objects. Convert them into the JSON format
  // expected by the Spring Boot request DTOs.
  if (body !== undefined) {
    configuration.headers["Content-Type"] = "application/json";
    configuration.body = JSON.stringify(body);
  }

  const response = await fetch(`${baseUrl}${path}`, configuration);

  if (!response.ok) {
    throw new Error(
      await readApiError(response, `Request failed with status ${response.status}.`)
    );
  }

  if (response.status === 204) return null;

  const contentType = response.headers.get("content-type") || "";
  return contentType.includes("application/json")
    ? response.json()
    : response.text();
}

export const businessApi = (path, options) =>
  request(BUSINESS_API_URL, path, options);

export const authApi = (path, options) =>
  request(AUTH_API_URL, path, options);

// Transaction Service uses the same HttpOnly JWT cookie as the other services.
export const transactionApi = (path, options) =>
  request(TRANSACTION_API_URL, path, options);
