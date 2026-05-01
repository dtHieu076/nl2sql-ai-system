package com.nl2sql_ai_system.nl2sql_backend.infrastructure.ai.service;

import com.nl2sql_ai_system.nl2sql_backend.chat.dto.AiAnalysisResponse;
import com.nl2sql_ai_system.nl2sql_backend.orchestrator.port.DataAnalyzerService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DataAnalyzerServiceImpl implements DataAnalyzerService {

    private final ChatClient chatClient;

    @Override
    public AiAnalysisResponse analyze(String userIntent, String rawJsonData) {
        // 1. Khởi tạo converter với class đích
        var outputConverter = new BeanOutputConverter<>(AiAnalysisResponse.class);

        // Lấy hướng dẫn định dạng JSON Schema mà Spring tự build từ record của mình
        String formatInstructions = outputConverter.getFormat();

        // 2. Viết Prompt
        String systemPrompt = """
                Bạn là một chuyên gia phân tích dữ liệu cấp cao.
                Nhiệm vụ của bạn là đọc Dữ liệu thô (JSON) và Trả lời câu hỏi của người dùng.

                QUY TẮC:
                1. Dựa vào dữ liệu để viết một báo cáo ngắn gọn, chuyên nghiệp bằng Markdown. Không tự bịa số liệu.
                2. Nếu người dùng muốn xem phân bổ (như "tỷ lệ", "cơ cấu") -> Tạo biểu đồ 'pie'.
                3. Nếu người dùng muốn xem xu hướng/so sánh (như "tháng", "năm") -> Tạo biểu đồ 'bar' hoặc 'line'.
                4. Nếu câu hỏi chỉ hỏi một con số (như "tổng doanh thu là bao nhiêu") -> Báo cáo Markdown là đủ, cấu hình biểu đồ để null.

                ĐỊNH DẠNG ĐẦU RA BẮT BUỘC:
                Bạn CHỈ ĐƯỢC trả về một chuỗi JSON hợp lệ tuân thủ chính xác cấu trúc sau.
                KHÔNG bọc JSON trong block code Markdown (như ```json).

                {format_instructions}
                """;

        // 3. Gọi AI
        String responseContent = chatClient.prompt()
                .system(s -> s.text(systemPrompt)
                        .param("format_instructions", formatInstructions))
                .user(u -> u.text("Câu hỏi: {intent}\nDữ liệu: {data}")
                        .param("intent", userIntent)
                        .param("data", rawJsonData))
                .call()
                .content();

        // 4. Parse JSON từ AI trả về thành Object Java
        return outputConverter.convert(responseContent);
    }
}