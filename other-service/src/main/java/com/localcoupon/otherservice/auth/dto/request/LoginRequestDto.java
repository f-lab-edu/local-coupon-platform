package com.localcoupon.otherservice.auth.dto.request;

import com.localcoupon.otherservice.common.util.validation.ValidEmail;
import jakarta.validation.constraints.NotBlank;

public record LoginRequestDto(@ValidEmail String email, @NotBlank String password) {
}
