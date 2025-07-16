package com.localcoupon.couponservice.user.dto.request;

public record SignUpRequestDto(String email, String password, String nickname, String address, String regionCode) {
    public static SignUpRequestDto of(String email, String password, String nickname, String address, String regionCode) {
        return new SignUpRequestDto(email, password, nickname, address, regionCode);
    }
}
