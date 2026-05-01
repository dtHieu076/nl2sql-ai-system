package com.nl2sql_ai_system.nl2sql_backend.infrastructure.ai.repository;

import com.nl2sql_ai_system.nl2sql_backend.infrastructure.ai.entity.BusinessRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BusinessRuleRepository extends JpaRepository<BusinessRule, Long> {
    List<BusinessRule> findByDataSourceId(Long dataSourceId);
}