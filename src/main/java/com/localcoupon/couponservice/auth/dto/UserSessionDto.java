package com.localcoupon.couponservice.auth.dto;

import com.localcoupon.couponservice.user.entity.User;

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

    public static UserSessionDto of(User user) {
        return new UserSessionDto(user.getId(), user.getEmail(), user.getNickname(), List.of(user.getRole().name()));
    }
}
