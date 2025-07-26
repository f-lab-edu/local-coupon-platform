package com.localcoupon.couponservice.user.service.impl;

import com.localcoupon.couponservice.user.dto.request.SignUpRequestDto;
import com.localcoupon.couponservice.user.dto.response.UserResponseDto;
import com.localcoupon.couponservice.user.entity.User;
import com.localcoupon.couponservice.user.enums.UserErrorCode;
import com.localcoupon.couponservice.user.exception.UserExcpetion;
import com.localcoupon.couponservice.user.exception.UserNotFoundException;
import com.localcoupon.couponservice.user.repository.UserRepository;
import com.localcoupon.couponservice.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserResponseDto signUpUser(SignUpRequestDto dto) {
        if (userRepository.existsByEmail(dto.email())) {
            throw new UserExcpetion(UserErrorCode.USER_ALREADY_EXISTS);
        }

        User savedUser = userRepository.save(User.from(dto));

        return UserResponseDto.fromEntity(savedUser);
    }

    @Override
    public UserResponseDto getUserByEmail(String userEmail) {
        return UserResponseDto.fromEntity(userRepository.findByEmail(userEmail).orElseThrow(() -> new UserNotFoundException(UserErrorCode.USER_NOT_FOUND)));
    }


}

