// Backend errors may be returned as JSON or plain text. This helper extracts a
// useful message in either format so users do not only see "Something went wrong".
export async function readApiError(response, fallbackMessage) {
  try {
    const contentType = response.headers.get("content-type") || "";

    if (contentType.includes("application/json")) {
      const body = await response.json();
      const validationMessages = body.validationErrors
        ? Object.values(body.validationErrors).join(" ")
        : "";

      return validationMessages || body.message || body.error || fallbackMessage;
    }

    const message = await response.text();
    return message.trim() || fallbackMessage;
  } catch {
    return fallbackMessage;
  }
}
