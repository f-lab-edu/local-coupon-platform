package com.localcoupon.couponservice.auth.service.impl;

import com.localcoupon.couponservice.auth.dto.UserSessionDto;
import com.localcoupon.couponservice.auth.dto.request.LoginRequestDto;
import com.localcoupon.couponservice.auth.dto.response.LoginResponseDto;
import com.localcoupon.couponservice.auth.dto.response.LogoutResponseDto;
import com.localcoupon.couponservice.auth.enums.AuthErrorCode;
import com.localcoupon.couponservice.auth.exception.PasswordNotMatchException;
import com.localcoupon.couponservice.auth.repository.SessionRepository;
import com.localcoupon.couponservice.auth.service.AuthService;
import com.localcoupon.couponservice.common.util.PasswordEncoder;
import com.localcoupon.couponservice.common.util.TokenGenerator;
import com.localcoupon.couponservice.user.entity.User;
import com.localcoupon.couponservice.user.enums.UserErrorCode;
import com.localcoupon.couponservice.user.exception.UserNotFoundException;
import com.localcoupon.couponservice.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;

    @Override
    public LoginResponseDto login(LoginRequestDto request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UserNotFoundException(UserErrorCode.USER_NOT_FOUND));

        if (!PasswordEncoder.decrypt(user.getPasswordEnc(), request.password())) {
            throw new PasswordNotMatchException(AuthErrorCode.PASSWORD_NOT_MATCHING);
        }

        String sessionToken = TokenGenerator.createSessionToken();

        sessionRepository.save(sessionToken, UserSessionDto.of(user));

        return LoginResponseDto.of(sessionToken);
    }

    @Override
    public LogoutResponseDto logout(String sessionToken) {
        sessionRepository.delete(sessionToken);
        return LogoutResponseDto.of(sessionToken);
    }
}
