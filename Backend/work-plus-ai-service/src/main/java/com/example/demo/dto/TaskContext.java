package com.example.demo.dto;

import java.time.LocalDate;

/** Only the safe Task fields required by the assistant are retained. */
public record TaskContext(
        Integer taskId,
        String taskName,
        String taskDescription,
        String projectName,
        String status,
        Integer progressPercent,
        LocalDate startDate,
        LocalDate endDate) {
}
