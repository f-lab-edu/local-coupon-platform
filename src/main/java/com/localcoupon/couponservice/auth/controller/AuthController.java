package com.localcoupon.couponservice.auth.controller;

import com.localcoupon.couponservice.auth.dto.request.LoginRequestDto;
import com.localcoupon.couponservice.auth.dto.response.LoginResponseDto;
import com.localcoupon.couponservice.auth.dto.response.LogoutResponseDto;
import com.localcoupon.couponservice.auth.service.AuthService;
import com.localcoupon.couponservice.common.constants.ApiMapping;
import com.localcoupon.couponservice.common.dto.response.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiMapping.AUTH)
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public SuccessResponse<LoginResponseDto> login(@RequestBody LoginRequestDto request) {
        return SuccessResponse.of(authService.login(request));
    }

    @PostMapping("/logout")
    public SuccessResponse<LogoutResponseDto> logout(@RequestHeader("Authorization") String token) {
        return SuccessResponse.of(authService.logout(token));
    }
}
