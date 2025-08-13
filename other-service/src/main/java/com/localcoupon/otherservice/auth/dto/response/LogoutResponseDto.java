package com.localcoupon.otherservice.auth.dto.response;

public record LogoutResponseDto(String token) {
    public static LogoutResponseDto of(String token) {
        return new LogoutResponseDto(token);
    }
}

