package com.nl2sql_ai_system.nl2sql_backend.common.context;

public class ChatContextHolder {
    private static final ThreadLocal<String> contextHolder = new ThreadLocal<>();

    public static void setRole(String role) {
        contextHolder.set(role);
    }

    public static String getRole() {
        return contextHolder.get();
    }

    public static void clear() {
        contextHolder.remove();
    }
}
