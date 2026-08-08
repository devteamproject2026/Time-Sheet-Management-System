package com.example.demo.dto;

/** Friendly answer plus the number of Employee Tasks used as context. */
public record ChatResponse(String answer, int taskCount) {
}
