package com.nl2sql_ai_system.nl2sql_backend.chat.strategy;

import com.nl2sql_ai_system.nl2sql_backend.chat.dto.ChatResult;

public interface ChatStrategy {
    enumQueryIntent getSupportedIntent();

    ChatResult process(String userQuery);
}