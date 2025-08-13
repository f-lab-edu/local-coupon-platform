package com.localcoupon.otherservice.auth.service.impl;

import com.localcoupon.otherservice.auth.dto.UserSessionDto;
import com.localcoupon.otherservice.auth.dto.request.LoginRequestDto;
import com.localcoupon.otherservice.auth.dto.response.LoginResponseDto;
import com.localcoupon.otherservice.auth.dto.response.LogoutResponseDto;
import com.localcoupon.otherservice.auth.enums.AuthErrorCode;
import com.localcoupon.otherservice.auth.exception.PasswordNotMatchException;
import com.localcoupon.otherservice.auth.repository.SessionRepository;
import com.localcoupon.otherservice.auth.service.AuthService;
import com.localcoupon.otherservice.common.util.PasswordEncoder;
import com.localcoupon.otherservice.common.util.TokenGenerator;
import com.localcoupon.otherservice.user.entity.User;
import com.localcoupon.otherservice.user.enums.UserErrorCode;
import com.localcoupon.otherservice.user.exception.UserNotFoundException;
import com.localcoupon.otherservice.user.repository.UserRepository;
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
