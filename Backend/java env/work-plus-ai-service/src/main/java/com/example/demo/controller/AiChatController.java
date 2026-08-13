package com.example.demo.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.ChatRequest;
import com.example.demo.dto.ChatResponse;
import com.example.demo.service.AiChatService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/ai")
public class AiChatController {
    private final AiChatService chatService;

    public AiChatController(AiChatService chatService) {
        this.chatService = chatService;
    }

    /**
     * Answers questions about only the logged-in Employee's Tasks. Employee ID
     * is intentionally not accepted from the request body.
     */
    @PostMapping("/chat")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ChatResponse chat(
            Authentication authentication,
            @CookieValue("jwt") String jwt,
            @Valid @RequestBody ChatRequest request) {
        return chatService.answer(authentication.getName(), "jwt=" + jwt, request.message());
    }
}
