package com.localcoupon.couponservice.auth.dto;

import com.localcoupon.couponservice.user.entity.User;
import org.springframework.util.Assert;

import java.util.List;

import static org.springframework.util.Assert.notNull;

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
        notNull(user, "user는 null일 수 없습니다.");
        notNull(user.getId(), "user.id는 필수입니다.");
        Assert.hasText(user.getEmail(), "user.email은 비어있을 수 없습니다.");
        return new UserSessionDto(user.getId(), user.getEmail(), user.getNickname(), List.of(user.getRole().name()));
    }
}
