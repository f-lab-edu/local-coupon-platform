package com.localcoupon.couponservice.auth.service;

import com.localcoupon.couponservice.auth.dto.request.LoginRequestDto;
import com.localcoupon.couponservice.auth.dto.response.LoginResponseDto;

public interface AuthService {
    LoginResponseDto login(LoginRequestDto request);
    void logout(String token);
    String getUserEmailByToken(String token);
    String getCurrentUserEmail();

}
