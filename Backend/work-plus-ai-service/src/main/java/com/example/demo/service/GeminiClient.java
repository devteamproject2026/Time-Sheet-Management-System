package com.example.demo.service;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import com.example.demo.exception.AiServiceException;
import tools.jackson.databind.JsonNode;

@Component
public class GeminiClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(GeminiClient.class);

    private final RestClient geminiClient;
    private final String apiKey;
    private final String model;

    public GeminiClient(
            @Value("${gemini.base-url}") String baseUrl,
            @Value("${gemini.api-key}") String apiKey,
            @Value("${gemini.model}") String model) {
        this.geminiClient = RestClient.create(baseUrl);
        this.apiKey = apiKey;
        this.model = model;
    }

    /** Sends minimal Task context to Gemini; the API key stays on the server. */
    public String generate(String prompt) {
        if (!StringUtils.hasText(apiKey)) {
            throw new AiServiceException(
                    "AI is not configured. Set GEMINI_API_KEY in the STS environment.");
        }

        Map<String, Object> request = Map.of(
                "contents", List.of(Map.of(
                        "role", "user",
                        "parts", List.of(Map.of("text", prompt)))),
                "generationConfig", Map.of(
                        "temperature", 0.1,
                        // Task lookup is simple, so keep reasoning low and reserve tokens for the answer.
                        "thinkingConfig", Map.of("thinkingLevel", "low"),
                        "maxOutputTokens", 1500));

        try {
            JsonNode response = geminiClient.post()
                    .uri("/v1beta/models/{model}:generateContent", model)
                    .header("x-goog-api-key", apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(JsonNode.class);

            JsonNode answer = response == null
                    ? null
                    : response.at("/candidates/0/content/parts/0/text");
            if (answer == null || answer.isMissingNode() || answer.asText().isBlank()) {
                throw new AiServiceException("Gemini returned an empty response. Please try again.");
            }
            return answer.asText().trim();
        } catch (AiServiceException exception) {
            throw exception;
        } catch (RestClientResponseException exception) {
            throw providerError(exception.getStatusCode().value(), exception);
        } catch (Exception exception) {
            // Log only technical diagnostic details. Never log the API key, prompt or task data.
            Throwable rootCause = rootCause(exception);
            LOGGER.error("Gemini request failed");
            LOGGER.error("Exception type: {}", exception.getClass().getName());
            LOGGER.error("Root cause type: {}", rootCause.getClass().getName());
            LOGGER.error("Root cause message: {}", safeMessage(rootCause));
            throw new AiServiceException(
                    "Could not connect to Gemini. Check the internet connection and try again.",
                    exception);
        }
    }

    private Throwable rootCause(Throwable exception) {
        Throwable current = exception;
        while (current.getCause() != null && current.getCause() != current) {
            current = current.getCause();
        }
        return current;
    }

    private String safeMessage(Throwable exception) {
        String message = exception.getMessage();
        if (!StringUtils.hasText(message)) return "No message provided";
        String redacted = StringUtils.hasText(apiKey)
                ? message.replace(apiKey, "[REDACTED]")
                : message;
        return redacted.length() > 500 ? redacted.substring(0, 500) : redacted;
    }

    /** Converts provider status codes into useful messages without leaking keys. */
    private AiServiceException providerError(int status, Exception cause) {
        String message = switch (status) {
            case 400 -> "Gemini rejected the request. Check the configured model.";
            case 401, 403 -> "Gemini rejected the API key. Create a valid key and update GEMINI_API_KEY.";
            case 404 -> "The configured Gemini model is unavailable for this API key.";
            case 429 -> "Gemini quota is exhausted. Wait or check the API project's quota and billing.";
            default -> "Gemini returned provider error " + status + ". Please try again later.";
        };
        return new AiServiceException(message, cause);
    }
}
