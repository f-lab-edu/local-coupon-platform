package com.localcoupon.couponservice.user.service;

import com.localcoupon.couponservice.user.dto.request.SignUpRequestDto;
import com.localcoupon.couponservice.user.dto.response.UserResponseDto;
import com.localcoupon.couponservice.user.entity.User;

public interface UserService {
    UserResponseDto signUpUser(SignUpRequestDto dto);
    UserResponseDto getUserByEmail(String userEmail);
}
