package com.nl2sql_ai_system.nl2sql_backend.infrastructure.metadata.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class GeminiEmbeddingClient {

    @Value("${gemini.api.key}")
    private String apiKey;

    // ✅ FIX 2: Inject RestTemplate thay vì khởi tạo bằng từ khóa new
    private final RestTemplate restTemplate;

    public List<Float> embed(String text) {
        // ✅ FIX 3: Cập nhật model nhúng mới nhất của Gemini
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-embedding-001:embedContent?key="
                + apiKey;

        Map<String, Object> request = Map.of(
                "content", Map.of(
                        "parts", List.of(
                                Map.of("text", text))));

        try {
            Map response = restTemplate.postForObject(url, request, Map.class);

            if (response == null || !response.containsKey("embedding")) {
                log.error("Gemini API error. Response: {}", response);
                throw new RuntimeException("Embedding API trả về lỗi hoặc null.");
            }

            Map embedding = (Map) response.get("embedding");
            List<Double> values = (List<Double>) embedding.get("values");

            return values.stream()
                    .map(Double::floatValue)
                    .toList();
        } catch (Exception e) {
            log.error("Exception when calling Gemini API", e);
            throw new RuntimeException("Lỗi khi gọi Gemini Embedding API", e);
        }
    }
}