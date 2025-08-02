package com.localcoupon.couponservice.user.dto.request;

import com.localcoupon.couponservice.common.util.validation.ValidEmail;
import jakarta.validation.constraints.NotBlank;

public record SignUpRequestDto(@ValidEmail String email, @NotBlank String password, String nickname, String address, String regionCode) {
    public static SignUpRequestDto of(String email, String password, String nickname, String address, String regionCode) {
        return new SignUpRequestDto(email, password, nickname, address, regionCode);
    }
}
