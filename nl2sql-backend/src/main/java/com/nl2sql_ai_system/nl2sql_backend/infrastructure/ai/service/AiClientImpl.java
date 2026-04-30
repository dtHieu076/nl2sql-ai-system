package com.nl2sql_ai_system.nl2sql_backend.infrastructure.ai.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import com.nl2sql_ai_system.nl2sql_backend.orchestrator.port.AiClient;

@Service
@RequiredArgsConstructor
public class AiClientImpl implements AiClient {

    private final ChatClient chatClient;

    @Override
    public String chat(String userQuery) {

        return chatClient.prompt()
                .system("""
                        Bạn là AI trợ lý cho hệ thống NL2SQL.

                        QUY TẮC:
                        - Nếu câu hỏi KHÔNG cần dữ liệu → trả lời trực tiếp
                        - Nếu câu hỏi CẦN dữ liệu từ database → BẮT BUỘC gọi tool executeDbQueryTool
                        - KHÔNG tự bịa dữ liệu
                        - KHÔNG tự tạo SQL nếu chưa có schema

                        Ví dụ:
                        - "doanh thu tháng này" → gọi tool
                        - "tổng lương nhân viên" → gọi tool
                        - "doanh thu là gì" → trả lời thường
                        - "giải thích SQL" → trả lời thường
                        """)
                .user(userQuery)
                .functions("executeDbQueryTool")
                .call()
                .content();
    }
}
