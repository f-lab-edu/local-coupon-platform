package com.localcoupon.couponservice.auth.dto.response;

public record LoginResponseDto(String token) {
    public static LoginResponseDto of (String token) {
        return new LoginResponseDto(token);
    }
}
