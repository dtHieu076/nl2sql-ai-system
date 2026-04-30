package com.nl2sql_ai_system.nl2sql_backend.infrastructure.ai.tool;

import com.nl2sql_ai_system.nl2sql_backend.common.context.ChatContextHolder;
import com.nl2sql_ai_system.nl2sql_backend.orchestrator.Nl2SqlOrchestrator;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

import java.util.function.Function;

@Configuration
@RequiredArgsConstructor
public class DbQueryToolConfig {

    private final Nl2SqlOrchestrator orchestrator;

    public record DbQueryRequest(String userIntent) {
    }

    @Bean
    @Description("""
            CHỈ gọi tool này khi:
            - Người dùng yêu cầu dữ liệu từ hệ thống database
            - Câu hỏi cần SQL để trả lời

            KHÔNG gọi khi:
            - Câu hỏi định nghĩa (ví dụ: "doanh thu là gì")
            - Câu hỏi kiến thức chung
            - Câu hỏi không cần dữ liệu thực

            Ví dụ gọi:
            - "doanh thu tháng này"
            - "tổng lương nhân viên"

            Ví dụ KHÔNG gọi:
            - "doanh thu là gì"
            - "giải thích SQL"
            """)
    public Function<DbQueryRequest, String> executeDbQueryTool() {

        return request -> {

            String role = ChatContextHolder.getRole();
            if (role == null)
                role = "GUEST";

            return orchestrator.process(request.userIntent(), role);
        };
    }
}