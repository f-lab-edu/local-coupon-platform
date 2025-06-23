package com.localcoupon.couponservice.auth.context;


public class AuthContextHolder {
    private static final ThreadLocal<String> userIdHolder = new ThreadLocal<>();

    public static void setUserKey(String userId) {
        userIdHolder.set(userId);
    }

    public static String getUserKey() {
        return userIdHolder.get();
    }

    public static void clear() {
        userIdHolder.remove();
    }
}
