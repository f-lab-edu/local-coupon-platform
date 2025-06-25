package com.localcoupon.couponservice.user.controller;

import com.localcoupon.couponservice.auth.context.AuthContextHolder;
import com.localcoupon.couponservice.global.constants.ApiMapping;
import static com.localcoupon.couponservice.global.constants.ApiMapping.API_Prefix;

import com.localcoupon.couponservice.global.dto.response.SuccessResponse;
import com.localcoupon.couponservice.user.dto.request.SignUpRequestDto;
import com.localcoupon.couponservice.user.dto.response.UserResponseDto;
import com.localcoupon.couponservice.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(API_Prefix.API_V1 + ApiMapping.USER )

public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    public SuccessResponse<UserResponseDto> getUser() {
        String userEmail = AuthContextHolder.getUserKey(); //TODO : 스프링 시큐리티 도입 시 수정
        return SuccessResponse.of(userService.getUserByEmail(userEmail));
    }

    @PostMapping
    public SuccessResponse<UserResponseDto> createUser(@RequestBody SignUpRequestDto request) {
        return SuccessResponse.of(userService.signUpUser(request));
    }

}
