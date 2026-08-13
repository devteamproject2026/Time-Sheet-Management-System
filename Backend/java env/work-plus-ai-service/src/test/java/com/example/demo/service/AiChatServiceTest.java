package com.example.demo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.dto.ChatResponse;
import com.example.demo.dto.TaskContext;

@ExtendWith(MockitoExtension.class)
class AiChatServiceTest {
    @Mock EmployeeTaskClient taskClient;
    @Mock GeminiClient geminiClient;

    @Test
    void sendsOnlyAuthenticatedEmployeeTaskContextToGemini() {
        TaskContext task = new TaskContext(
                9,
                "Prepare report",
                "Weekly status report",
                "Space Technology",
                "IN_PROGRESS",
                60,
                LocalDate.of(2026, 8, 1),
                LocalDate.of(2026, 8, 8));
        when(taskClient.getMyTasks("jwt=test-token")).thenReturn(List.of(task));
        when(geminiClient.generate(contains("Prepare report")))
                .thenReturn("Your report Task is 60% complete.");

        AiChatService service = new AiChatService(taskClient, geminiClient);
        ChatResponse response = service.answer(
                "employee1", "jwt=test-token", "What is pending?");

        assertThat(response.taskCount()).isEqualTo(1);
        assertThat(response.answer()).contains("60%");
        verify(taskClient).getMyTasks("jwt=test-token");
        verify(geminiClient).generate(contains("Logged-in Employee: employee1"));
    }
}
