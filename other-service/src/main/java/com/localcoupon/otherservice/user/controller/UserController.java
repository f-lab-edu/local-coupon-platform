package com.localcoupon.otherservice.user.controller;

import com.localcoupon.common.constants.ApiMapping;
import com.localcoupon.common.dto.response.SuccessResponse;
import com.localcoupon.otherservice.user.dto.request.SignUpRequestDto;
import com.localcoupon.otherservice.user.dto.response.UserResponseDto;
import com.localcoupon.otherservice.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiMapping.USER)

public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    public SuccessResponse<UserResponseDto> getUser(@RequestHeader("X-USER-EMAIL") String userEmail) {
        return SuccessResponse.of(userService.getUserByEmail(userEmail));
    }

    @PostMapping("/signup")
    public SuccessResponse<UserResponseDto> createUser(@RequestBody SignUpRequestDto request) {
        return SuccessResponse.of(userService.signUpUser(request));
    }

}
