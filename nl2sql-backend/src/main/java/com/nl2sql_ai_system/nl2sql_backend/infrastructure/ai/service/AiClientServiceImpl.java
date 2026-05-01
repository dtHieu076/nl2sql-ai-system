package com.nl2sql_ai_system.nl2sql_backend.infrastructure.ai.service;

import lombok.RequiredArgsConstructor;
import java.util.Map;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import com.nl2sql_ai_system.nl2sql_backend.orchestrator.port.AiClientService;

@Service
@RequiredArgsConstructor
public class AiClientServiceImpl implements AiClientService {

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

    @Override
    public String generateSql(String userIntent, String schemaContext) {
        String systemPrompt = """
                Bạn là chuyên gia về PostgreSQL.
                Dựa trên Schema dưới đây, hãy tạo một câu lệnh SQL chính xác để trả lời câu hỏi.

                SCHEMA:
                %s

                QUY TẮC:
                1. CHỈ trả về câu lệnh SQL thuần túy.
                2. KHÔNG giải thích, KHÔNG dùng Markdown (ví dụ: ```sql).
                3. Nếu không thể tạo SQL từ schema, hãy trả về chuỗi: 'ERROR'.
                4. Sử dụng alias cho bảng nếu cần thiết để câu lệnh rõ ràng.
                """.formatted(schemaContext);

        return chatClient.prompt()
                .system(systemPrompt)
                .user(userIntent)
                .call()
                .content()
                .trim();
    }
}
