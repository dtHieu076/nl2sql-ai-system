package com.nl2sql_ai_system.nl2sql_backend.infrastructure.export.strategy;

import org.springframework.stereotype.Component;

import com.nl2sql_ai_system.nl2sql_backend.infrastructure.ai.dto.AiAnalysisResponse;

@Component
public class PdfExportStrategyImpl implements FileExportStrategy {

    @Override
    public String getFileType() {
        return "pdf";
    }

    @Override
    public byte[] export(AiAnalysisResponse data) {
        /*
         * HƯỚNG DẪN TÍCH HỢP THƯ VIỆN THỰC TẾ (VD: Dùng OpenPDF):
         * * try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
         * Document document = new Document();
         * PdfWriter.getInstance(document, baos);
         * document.open();
         * * // 1. Thêm Tiêu đề
         * document.add(new Paragraph("BÁO CÁO PHÂN TÍCH DỮ LIỆU"));
         * * // 2. Chèn nội dung Markdown (Có thể dùng thư viện flexmark-java để convert
         * Markdown -> HTML -> PDF)
         * document.add(new Paragraph(data.markdownReport()));
         * * // 3. Xử lý Biểu đồ (Nếu có)
         * if (data.chartConfig() != null) {
         * // Dùng JFreeChart để render cấu hình chartConfig thành file ảnh (png/jpg)
         * // Sau đó chèn ảnh đó vào document:
         * document.add(Image.getInstance(chartImageBytes));
         * }
         * * document.close();
         * return baos.toByteArray();
         * } catch (Exception e) {
         * throw new RuntimeException("Lỗi khi tạo file PDF: " + e.getMessage());
         * }
         */

        System.out.println("Đang xử lý tạo file PDF...");

        String dummyContent = "--- BÁO CÁO PDF GIẢ LẬP ---\n" + data.markdownReport();
        return dummyContent.getBytes();
    }
}