package com.nl2sql_ai_system.nl2sql_backend.chat.service;

import com.nl2sql_ai_system.nl2sql_backend.chat.dto.ChatResult;

public interface QueryService {
    ChatResult handleChat(String userQuery, String role);
}
