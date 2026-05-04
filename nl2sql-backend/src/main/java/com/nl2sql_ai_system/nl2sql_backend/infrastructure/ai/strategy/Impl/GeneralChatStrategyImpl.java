package com.nl2sql_ai_system.nl2sql_backend.infrastructure.ai.strategy.Impl;

import org.springframework.stereotype.Component;

import com.nl2sql_ai_system.nl2sql_backend.infrastructure.ai.dto.ChatResult;
import com.nl2sql_ai_system.nl2sql_backend.infrastructure.ai.dto.enumQueryIntent;
import com.nl2sql_ai_system.nl2sql_backend.infrastructure.ai.strategy.ChatStrategy;
import com.nl2sql_ai_system.nl2sql_backend.orchestrator.port.AiClientService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GeneralChatStrategyImpl implements ChatStrategy {

    private final AiClientService aiClientService; // Inject Port

    @Override
    public enumQueryIntent getSupportedIntent() {
        return enumQueryIntent.GENERAL;
    }

    @Override
    public ChatResult process(String userQuery) {
        String response = aiClientService.generateSimpleResponse(userQuery); // Gọi qua Port
        return ChatResult.builder()
                .type(enumQueryIntent.GENERAL.name())
                .message(response)
                .build();
    }
}