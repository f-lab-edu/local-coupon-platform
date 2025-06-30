package com.localcoupon.couponservice.auth.dto;

import java.util.List;

public record UserSessionDto(
        Long id,
        String email,
        String nickname,
        List<String> roles
) {
    public UserSessionDto {
        roles = List.copyOf(roles);
    }
}
