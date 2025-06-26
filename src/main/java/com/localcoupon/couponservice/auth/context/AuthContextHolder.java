package com.localcoupon.couponservice.auth.context;


public class AuthContextHolder {
    private static final String NO_AUTH = "NO_AUTH";
    private static final ThreadLocal<String> userIdHolder = new ThreadLocal<>();

    public static void setUserKey(String userId) {
        userIdHolder.set(userId);
    }

    public static String getUserKey() {
        return userIdHolder.get() != null ? userIdHolder.get() : NO_AUTH;
    }

    public static void clear() {
        userIdHolder.remove();
    }
}
