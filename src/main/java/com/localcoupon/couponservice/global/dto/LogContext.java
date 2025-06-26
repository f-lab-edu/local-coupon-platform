package com.localcoupon.couponservice.global.dto;

import com.localcoupon.couponservice.auth.context.AuthContextHolder;
import jakarta.servlet.http.HttpServletRequest;

public record LogContext(
        String userId,
        String uri,
        String method,
        String ip,
        String action,
        Throwable exception
) {

    public static LogContext of(HttpServletRequest request, String action, Throwable e) {
        String userId = AuthContextHolder.getUserKey();
        return new LogContext(
                userId,
                request.getRequestURI(),
                request.getMethod(),
                extractClientIp(request),
                action,
                e
        );
    }

    private static String extractClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
