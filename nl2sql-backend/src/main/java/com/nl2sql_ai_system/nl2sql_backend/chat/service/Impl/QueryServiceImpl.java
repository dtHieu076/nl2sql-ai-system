package com.nl2sql_ai_system.nl2sql_backend.chat.service.Impl;

import com.nl2sql_ai_system.nl2sql_backend.chat.dto.ChatResult;
import com.nl2sql_ai_system.nl2sql_backend.chat.service.QueryService;
import com.nl2sql_ai_system.nl2sql_backend.chat.strategy.ChatStrategy;
import com.nl2sql_ai_system.nl2sql_backend.chat.strategy.enumQueryIntent;
import com.nl2sql_ai_system.nl2sql_backend.common.context.ChatContextHolder;
import com.nl2sql_ai_system.nl2sql_backend.orchestrator.port.AiClientService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class QueryServiceImpl implements QueryService {

    private final AiClientService aiClientService;
    private final Map<enumQueryIntent, ChatStrategy> strategyMap;

    public QueryServiceImpl(AiClientService aiClientService, List<ChatStrategy> strategies) {
        this.aiClientService = aiClientService;
        this.strategyMap = strategies.stream()
                .collect(Collectors.toMap(ChatStrategy::getSupportedIntent, s -> s));
    }

    @Override
    public ChatResult handleChat(String userQuery, String role) {
        // 1. Lưu Role vào ThreadLocal cho Tool sử dụng
        ChatContextHolder.setRole(role);

        try {
            // 2. Dùng AI Hạ tầng để phân tích ngữ nghĩa (Semantic Router)
            enumQueryIntent intent = aiClientService.classifyIntent(userQuery);

            // 3. Lấy chiến lược tương ứng (Fallback về GENERAL nếu lỗi)
            ChatStrategy strategy = strategyMap.getOrDefault(intent, strategyMap.get(enumQueryIntent.GENERAL));

            // 4. Ủy quyền thực thi cho Strategy
            return strategy.process(userQuery);

        } finally {
            // 5. Luôn dọn dẹp Context để tránh rò rỉ bộ nhớ
            ChatContextHolder.clear();
        }
    }
}