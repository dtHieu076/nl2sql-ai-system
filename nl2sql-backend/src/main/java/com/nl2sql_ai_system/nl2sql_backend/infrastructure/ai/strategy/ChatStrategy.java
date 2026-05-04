package com.nl2sql_ai_system.nl2sql_backend.infrastructure.ai.strategy;

import com.nl2sql_ai_system.nl2sql_backend.infrastructure.ai.dto.ChatResult;
import com.nl2sql_ai_system.nl2sql_backend.infrastructure.ai.dto.enumQueryIntent;

public interface ChatStrategy {
    enumQueryIntent getSupportedIntent();

    ChatResult process(String userQuery);
}