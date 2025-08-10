package com.localcoupon.otherservice.user.dto.response;

import com.localcoupon.otherservice.user.entity.User;

public record UserResponseDto(String email, String nickname, String address, String regionCode) {
    public static UserResponseDto fromEntity(User user) {
        return new UserResponseDto(
                user.getEmail(),
                user.getNickname(),
                user.getAddress(),
                user.getRegionCode()
        );
    }

}
