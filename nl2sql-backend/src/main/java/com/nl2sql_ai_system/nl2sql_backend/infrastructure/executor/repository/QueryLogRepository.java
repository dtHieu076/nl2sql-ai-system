package com.nl2sql_ai_system.nl2sql_backend.infrastructure.executor.repository;

import com.nl2sql_ai_system.nl2sql_backend.infrastructure.executor.entity.QueryLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QueryLogRepository extends JpaRepository<QueryLog, Long> {
    // Lưu vết các câu truy vấn để fine-tune hoặc audit sau này
}