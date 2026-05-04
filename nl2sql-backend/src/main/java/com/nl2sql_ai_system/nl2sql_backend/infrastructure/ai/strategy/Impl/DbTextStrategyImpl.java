package com.nl2sql_ai_system.nl2sql_backend.infrastructure.ai.strategy.Impl;

import org.springframework.stereotype.Component;

import com.nl2sql_ai_system.nl2sql_backend.infrastructure.ai.dto.ChatResult;
import com.nl2sql_ai_system.nl2sql_backend.infrastructure.ai.dto.enumQueryIntent;
import com.nl2sql_ai_system.nl2sql_backend.infrastructure.ai.strategy.ChatStrategy;
import com.nl2sql_ai_system.nl2sql_backend.orchestrator.port.AiClientService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DbTextStrategyImpl implements ChatStrategy {

    private final AiClientService aiClientService; // Inject Port

    @Override
    public enumQueryIntent getSupportedIntent() {
        return enumQueryIntent.DB_TEXT;
    }

    @Override
    public ChatResult process(String userQuery) {
        String response = aiClientService.chatWithDbTool(userQuery);
        return ChatResult.builder()
                .type(enumQueryIntent.DB_TEXT.name())
                .message(response)
                .build();
    }
}