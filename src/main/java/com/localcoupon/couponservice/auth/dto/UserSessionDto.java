package com.localcoupon.couponservice.auth.dto;

import java.util.List;

public record UserSessionDto(String email, List<String> roles) {
    public UserSessionDto {
        roles = List.copyOf(roles);
    }
}

