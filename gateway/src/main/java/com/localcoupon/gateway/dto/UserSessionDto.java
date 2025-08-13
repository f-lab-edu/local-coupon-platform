package com.localcoupon.gateway.dto;

import java.util.List;

public record UserSessionDto(
        Long id,
        String email,
        String nickname,
        List<String> roles
) {
    public UserSessionDto(Long id, String email, String nickname, List<String> roles) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        this.roles = (roles == null) ? List.of() : List.copyOf(roles);
    }
}
