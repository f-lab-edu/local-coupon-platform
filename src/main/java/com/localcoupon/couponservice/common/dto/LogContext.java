package com.localcoupon.couponservice.common.dto;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public record LogContext(
        String userId,
        String uri,
        String method,
        String ip,
        String action,
        Throwable exception
) {

    public static LogContext of(HttpServletRequest request, String action, Throwable e) {
        return new LogContext(
                getUserIdFromSecurityContext(),
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

    private static String getUserIdFromSecurityContext() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            Object principal = auth.getPrincipal();
            if (principal instanceof String) {
                return (String) principal;
            }
        }
        return "NO_AUTH";
    }
}
