package com.localcoupon.couponservice.auth.controller;

import com.localcoupon.couponservice.auth.dto.request.LoginRequestDto;
import com.localcoupon.couponservice.auth.service.AuthService;
import com.localcoupon.couponservice.global.constants.ApiMapping;
import static com.localcoupon.couponservice.global.constants.ApiMapping.API_Prefix;
import com.localcoupon.couponservice.global.response.SuccessResponse;
import com.localcoupon.couponservice.auth.dto.response.LoginResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(API_Prefix.API_V1 + ApiMapping.AUTH)
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public SuccessResponse<LoginResponseDto> login(@RequestBody LoginRequestDto request) {
        return SuccessResponse.of(authService.login(request));
    }

    @PostMapping("/logout")
    public SuccessResponse<Void> logout(@RequestHeader("Authorization") String token) {
        authService.logout(token);
        return SuccessResponse.of(null);
    }
}
