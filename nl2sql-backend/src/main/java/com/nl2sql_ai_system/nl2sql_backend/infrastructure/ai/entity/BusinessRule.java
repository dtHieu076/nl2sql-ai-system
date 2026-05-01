package com.nl2sql_ai_system.nl2sql_backend.infrastructure.ai.entity;

import com.nl2sql_ai_system.nl2sql_backend.infrastructure.datasource.entity.DataSource;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "business_rules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BusinessRule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "data_source_id")
    private DataSource dataSource;

    private String ruleName;
    private String ruleDefinition;
    private LocalDateTime createdAt = LocalDateTime.now();
}