package com.nl2sql_ai_system.nl2sql_backend.chat.service;

import com.nl2sql_ai_system.nl2sql_backend.common.context.ChatContextHolder;
import com.nl2sql_ai_system.nl2sql_backend.orchestrator.port.AiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final AiClient aiClient;

    @Override
    public String handleChat(String userQuery, String role) {

        ChatContextHolder.setRole(role);

        try {
            return aiClient.chat(userQuery);
        } finally {
            ChatContextHolder.clear();
        }
    }
}