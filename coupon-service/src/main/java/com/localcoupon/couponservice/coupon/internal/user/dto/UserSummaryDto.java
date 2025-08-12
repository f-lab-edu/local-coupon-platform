package com.localcoupon.couponservice.coupon.internal.user.dto;

public record UserSummaryDto(Long id, String email, String nickname, String role) {
    public static UserSummaryDto of(Long id, String email, String nickname, String role) {
        return new UserSummaryDto(id, email, nickname, role);
    }
}