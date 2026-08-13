package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Question entered by the logged-in Employee. */
public record ChatRequest(
        @NotBlank(message = "Message is required")
        @Size(max = 500, message = "Message must not exceed 500 characters")
        String message) {
}
