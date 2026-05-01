package com.nl2sql_ai_system.nl2sql_backend.infrastructure.executor.service;

import com.nl2sql_ai_system.nl2sql_backend.infrastructure.datasource.entity.DataSource;
import com.nl2sql_ai_system.nl2sql_backend.orchestrator.port.DataSourceService;
import com.nl2sql_ai_system.nl2sql_backend.orchestrator.port.ExecutorService;
import com.nl2sql_ai_system.nl2sql_backend.infrastructure.executor.pool.DataSourcePoolManager;
import com.nl2sql_ai_system.nl2sql_backend.infrastructure.executor.validator.SqlValidator;
import com.nl2sql_ai_system.nl2sql_backend.infrastructure.executor.formatter.QueryResultFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ExecutorServiceImpl implements ExecutorService {

  private final DataSourceService dataSourceService;
  private final DataSourcePoolManager poolManager;
  private final SqlValidator sqlValidator;
  private final QueryResultFormatter resultFormatter;

  @Override
  public String execute(String sql, Long dataSourceId) {
    try {
      // 1. Validate SQL (Bảo mật)
      sqlValidator.validate(sql);

      // 2. Lấy Connection từ Pool (Hiệu suất)
      DataSource dsInfo = dataSourceService.findById(dataSourceId);
      javax.sql.DataSource dataSource = poolManager.getDataSource(dsInfo);

      // 3. Thực thi truy vấn
      JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

      // Giới hạn số dòng ở mức Database để tránh crash RAM nếu query ra 1 triệu dòng
      jdbcTemplate.setMaxRows(100);
      List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);

      // 4. Định dạng dữ liệu trả về cho AI (Token Limit)
      return resultFormatter.format(sql, dataSourceId, rows);

    } catch (Exception e) {
      return String.format("{\"status\": \"error\", \"message\": \"%s\"}", e.getMessage());
    }
  }
}