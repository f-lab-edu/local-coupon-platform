package com.localcoupon.couponservice.auth.service;

import com.localcoupon.couponservice.auth.dto.request.LoginRequestDto;
import com.localcoupon.couponservice.auth.dto.response.LoginResponseDto;
import com.localcoupon.couponservice.auth.dto.response.LogoutResponseDto;

public interface AuthService {
    LoginResponseDto login(LoginRequestDto request);
    LogoutResponseDto logout(String token);
}
