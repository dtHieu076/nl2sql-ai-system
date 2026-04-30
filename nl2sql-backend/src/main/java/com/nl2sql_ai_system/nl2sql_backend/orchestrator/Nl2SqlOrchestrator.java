package com.nl2sql_ai_system.nl2sql_backend.orchestrator;

public interface Nl2SqlOrchestrator {
    String process(String intent, String role);
}