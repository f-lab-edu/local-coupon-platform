package com.localcoupon.couponservice.auth.service.impl;

import com.localcoupon.couponservice.auth.context.AuthContextHolder;
import com.localcoupon.couponservice.auth.dto.request.LoginRequestDto;
import com.localcoupon.couponservice.auth.dto.response.LoginResponseDto;
import com.localcoupon.couponservice.auth.dto.response.LogoutResponseDto;
import com.localcoupon.couponservice.auth.enums.AuthErrorCode;
import com.localcoupon.couponservice.auth.exception.PasswordNotMatchException;
import com.localcoupon.couponservice.auth.service.AuthService;
import com.localcoupon.couponservice.global.util.PasswordEncoder;
import com.localcoupon.couponservice.global.util.TokenGenerator;
import com.localcoupon.couponservice.user.entity.User;
import com.localcoupon.couponservice.user.enums.UserErrorCode;
import com.localcoupon.couponservice.user.exception.UserNotFoundException;
import com.localcoupon.couponservice.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private static final String REDIS_SESSION_PREFIX = "SESSION:";

    private final UserRepository userRepository;
    private final StringRedisTemplate redisTemplate;
    private final Duration SESSION_TTL = Duration.ofHours(1); // 1시간 TTL

    public LoginResponseDto login(LoginRequestDto request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UserNotFoundException(UserErrorCode.USER_NOT_FOUND));

        if (!PasswordEncoder.decrypt(user.getPasswordEnc(), request.password())) {
            throw new PasswordNotMatchException(AuthErrorCode.PASSWORD_NOT_MATCHING);
        }

        //TODO: 안전한 토큰으로 전환 필요
        String sessionToken = TokenGenerator.createSessionToken();
        redisTemplate.opsForValue().set(REDIS_SESSION_PREFIX + sessionToken, user.getEmail(), SESSION_TTL);
        return new LoginResponseDto(sessionToken);
    }

    public LogoutResponseDto logout(String sessionToken) {
        redisTemplate.delete(REDIS_SESSION_PREFIX + sessionToken);
        return new LogoutResponseDto(sessionToken);
    }

    public String getUserEmailByToken(String sessionToken) {
        String userEmail = redisTemplate.opsForValue().get(REDIS_SESSION_PREFIX + sessionToken);
        return String.valueOf(userEmail);
    }

    @Override
    public String getCurrentUserEmail() {
        return AuthContextHolder.getUserKey();
    }
}

