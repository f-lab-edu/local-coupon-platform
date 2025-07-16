package com.localcoupon.couponservice.auth.dto.response;

public record LogoutResponseDto(String token) {
    public static LogoutResponseDto of(String token) {
        return new LogoutResponseDto(token);
    }
}

