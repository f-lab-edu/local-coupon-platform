package com.localcoupon.couponservice.global.dto;

public record LogContext(
        String uri,
        String ip,
        String method,
        String userId,
        Throwable exception
) {
    public static LogContext of(String uri, String ip, String method, String userId, Throwable exception) {
        return new LogContext(uri, ip, method, userId, exception);
    }
}
