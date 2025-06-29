package com.localcoupon.couponservice.user.controller;

import com.localcoupon.couponservice.common.constants.ApiMapping;
import com.localcoupon.couponservice.common.dto.response.SuccessResponse;
import com.localcoupon.couponservice.user.dto.request.SignUpRequestDto;
import com.localcoupon.couponservice.user.dto.response.UserResponseDto;
import com.localcoupon.couponservice.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiMapping.USER)

public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    public SuccessResponse<UserResponseDto> getUser(@AuthenticationPrincipal String userEmail) {
        return SuccessResponse.of(userService.getUserByEmail(userEmail));
    }

    @PostMapping("/signup")
    public SuccessResponse<UserResponseDto> createUser(@RequestBody SignUpRequestDto request) {
        return SuccessResponse.of(userService.signUpUser(request));
    }

}
