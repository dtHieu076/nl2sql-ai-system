package com.nl2sql_ai_system.nl2sql_backend.chat.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import com.nl2sql_ai_system.nl2sql_backend.chat.service.ChatService;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class QueryController {

    private final ChatService chatService;

    @PostMapping
    public String chat(@RequestBody ChatRequest request) {
        return chatService.handleChat(
                request.getUserQuery(),
                request.getRole());
    }

    public static class ChatRequest {
        private String userQuery;
        private String role;

        public String getUserQuery() {
            return userQuery;
        }

        public void setUserQuery(String userQuery) {
            this.userQuery = userQuery;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }
    }
}