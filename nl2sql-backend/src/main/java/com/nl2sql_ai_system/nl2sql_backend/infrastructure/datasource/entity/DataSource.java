package com.nl2sql_ai_system.nl2sql_backend.infrastructure.datasource.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "data_sources")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DataSource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String dbType;
    private String jdbcUrl;
    private String driverClassName;
    private String username;
    private String password;

    @Enumerated(EnumType.STRING)
    private enumDataSourceStatus status;

    private LocalDateTime lastSyncAt;
    private String assignedRole; // Ví dụ: "HR", "SALES", "ADMIN"
    private LocalDateTime createdAt = LocalDateTime.now();
}