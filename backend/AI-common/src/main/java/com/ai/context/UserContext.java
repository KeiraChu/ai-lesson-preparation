package com.ai.context;

public final class UserContext {
    private static final ThreadLocal<Long> CURRENT_USER = new ThreadLocal<>();

    private UserContext() {}

    public static void setUserId(Long userId) {
        CURRENT_USER.set(userId);
    }

    public static Long getUserId() {
        Long userId = CURRENT_USER.get();
        if (userId == null) {
            throw new IllegalStateException("未获取到当前用户身份");
        }
        return userId;
    }

    public static void clear() {
        CURRENT_USER.remove();
    }
}

