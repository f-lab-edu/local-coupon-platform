package com.localcoupon.couponservice.auth.dto.request;

import com.localcoupon.couponservice.common.util.validation.ValidEmail;
import jakarta.validation.constraints.NotBlank;

public record LoginRequestDto(@ValidEmail String email, @NotBlank String password) {
}
