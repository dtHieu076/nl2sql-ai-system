package com.nl2sql_ai_system.nl2sql_backend.infrastructure.ai.repository;

import com.nl2sql_ai_system.nl2sql_backend.infrastructure.ai.entity.PromptTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PromptTemplateRepository extends JpaRepository<PromptTemplate, Long> {
    Optional<PromptTemplate> findByNameAndIsActiveTrue(String name);
}