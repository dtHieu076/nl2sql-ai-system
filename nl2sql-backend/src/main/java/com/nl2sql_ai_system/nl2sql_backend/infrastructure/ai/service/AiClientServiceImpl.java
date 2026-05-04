package com.nl2sql_ai_system.nl2sql_backend.infrastructure.ai.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import com.nl2sql_ai_system.nl2sql_backend.infrastructure.ai.dto.enumQueryIntent;
import com.nl2sql_ai_system.nl2sql_backend.orchestrator.port.AiClientService;

@Service
@RequiredArgsConstructor
public class AiClientServiceImpl implements AiClientService {

    private final ChatClient chatClient;

    @Override
    public String generateSimpleResponse(String userQuery) {
        return chatClient.prompt()
                .system("""
                        Bạn là trợ lý AI thông minh.
                        Hãy trả lời câu hỏi của người dùng một cách ngắn gọn, lịch sự.
                        Lưu ý: Bạn không có quyền truy cập trực tiếp vào cơ sở dữ liệu trong chế độ này.
                        """)
                .user(userQuery)
                .call()
                .content();
    }

    @Override
    public String chatWithDbTool(String userQuery) {
        return chatClient.prompt()
                .system("""
                        Bạn là AI trợ lý cho hệ thống NL2SQL.

                        QUY TẮC:
                        - Nếu câu hỏi cần dữ liệu thực tế từ hệ thống -> BẮT BUỘC gọi tool 'executeDbQueryTool'.
                        - Sau khi tool trả về dữ liệu, hãy tổng hợp và trả lời người dùng một cách tự nhiên.
                        - KHÔNG tự bịa ra số liệu nếu tool không trả về kết quả.
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

    @Override
    public enumQueryIntent classifyIntent(String userQuery) {
        String prompt = """
                Phân loại câu hỏi sau thành 1 trong 3 loại:
                1. GENERAL: Câu hỏi chào hỏi, định nghĩa, không cần số liệu thực tế. (VD: "Doanh thu là gì", "Xin chào")
                2. DB_TEXT: Cần lấy số liệu nhưng chỉ là câu hỏi tra cứu đơn giản, một con số. (VD: "Tổng doanh thu tháng 1 là bao nhiêu")
                3. DB_REPORT: Cần lấy số liệu và có tính chất so sánh, phân tích, thống kê, xem xu hướng, tỷ lệ, cần vẽ biểu đồ. (VD: "So sánh doanh thu các tháng")

                CHỈ TRẢ VỀ TÊN LOẠI: GENERAL, DB_TEXT, hoặc DB_REPORT. Không giải thích gì thêm.
                Câu hỏi: "%s"
                """;

        String classification = chatClient.prompt()
                .user(String.format(prompt, userQuery))
                .call()
                .content()
                .trim()
                .toUpperCase();

        try {
            return enumQueryIntent.valueOf(classification);
        } catch (IllegalArgumentException e) {
            return enumQueryIntent.GENERAL; // Mặc định an toàn
        }
    }
}
