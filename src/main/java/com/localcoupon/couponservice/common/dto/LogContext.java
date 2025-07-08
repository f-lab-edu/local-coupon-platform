package com.localcoupon.couponservice.common.dto;

import com.localcoupon.couponservice.auth.security.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public record LogContext(
        String userId,
        String uri,
        String method,
        String ip,
        String action
) {

    public static LogContext of(HttpServletRequest request, String action) {
        return new LogContext(
                getUserEmailFromSecurityContext(),
                request.getRequestURI(),
                request.getMethod(),
                extractClientIp(request),
                action
        );
    }

    private static String extractClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private static String getUserEmailFromSecurityContext() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .filter(Authentication::isAuthenticated)
                .map(Authentication::getPrincipal)
                .filter(principal -> principal instanceof CustomUserDetails)
                .map(principal -> ((CustomUserDetails) principal).getEmail())
                .map(String::valueOf)
                .orElse("NO_AUTH");
    }
}
