package com.localcoupon.otherservice.auth.service;

import com.localcoupon.otherservice.auth.dto.request.LoginRequestDto;
import com.localcoupon.otherservice.auth.dto.response.LoginResponseDto;
import com.localcoupon.otherservice.auth.dto.response.LogoutResponseDto;

public interface AuthService {
    LoginResponseDto login(LoginRequestDto request);
    LogoutResponseDto logout(String token);
}
