package com.localcoupon.couponservice.user.service.impl;

import com.localcoupon.couponservice.global.util.PasswordEncoder;
import com.localcoupon.couponservice.user.dto.request.SignUpRequestDto;
import com.localcoupon.couponservice.user.dto.response.UserResponseDto;
import com.localcoupon.couponservice.user.entity.User;
import com.localcoupon.couponservice.user.enums.UserErrorCode;
import com.localcoupon.couponservice.user.exception.UserAlreadyExistsException;
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
            throw new UserAlreadyExistsException(UserErrorCode.USER_ALREADY_EXISTS);
        }

        User newUser = User.builder()
                .email(dto.email())
                .passwordEnc(getEncryptionPassword(dto.password()))
                .nickname(dto.nickname())
                .address(dto.address())
                .regionCode(dto.regionCode())
                .build();

        User savedUser = userRepository.save(newUser);

        return UserResponseDto.fromEntity(savedUser);
    }


    private String getEncryptionPassword(String password) {
        return PasswordEncoder.hash(password);
    }

    @Override
    public UserResponseDto getUserByEmail(String userEmail) {
        return UserResponseDto.fromEntity(userRepository.findByEmail(userEmail).orElseThrow(() -> new UserNotFoundException(UserErrorCode.USER_NOT_FOUND)));
    }


}

