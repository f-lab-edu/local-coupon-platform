package com.localcoupon.otherservice.user.service;

import com.localcoupon.otherservice.user.dto.request.SignUpRequestDto;
import com.localcoupon.otherservice.user.dto.response.UserResponseDto;

public interface UserService {
    UserResponseDto signUpUser(SignUpRequestDto dto);
    UserResponseDto getUserByEmail(String userEmail);
}
