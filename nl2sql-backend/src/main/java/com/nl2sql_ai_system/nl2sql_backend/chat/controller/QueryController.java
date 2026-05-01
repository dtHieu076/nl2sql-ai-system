package com.nl2sql_ai_system.nl2sql_backend.chat.controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.web.bind.annotation.*;

import com.nl2sql_ai_system.nl2sql_backend.chat.dto.ChatResult;
import com.nl2sql_ai_system.nl2sql_backend.chat.service.QueryService;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class QueryController {

    private final QueryService queryService;

    @PostMapping
    public ChatResult chat(@RequestBody ChatRequest request) {
        return queryService.handleChat(
                request.getUserQuery(),
                request.getRole());
    }

    @Getter
    @Setter
    public static class ChatRequest {
        private String userQuery;
        private String role;
    }
}