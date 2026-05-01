package com.nl2sql_ai_system.nl2sql_backend.infrastructure.executor.validator;

public class SqlValidationException extends RuntimeException {

    public SqlValidationException(String message) {
        super(message);
    }
}