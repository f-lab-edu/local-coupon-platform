package com.localcoupon.couponservice.auth.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.localcoupon.couponservice.auth.dto.UserSessionDto;
import com.localcoupon.couponservice.auth.dto.request.LoginRequestDto;
import com.localcoupon.couponservice.auth.dto.response.LoginResponseDto;
import com.localcoupon.couponservice.auth.dto.response.LogoutResponseDto;
import com.localcoupon.couponservice.auth.enums.AuthErrorCode;
import com.localcoupon.couponservice.auth.exception.PasswordNotMatchException;
import com.localcoupon.couponservice.auth.service.AuthService;
import com.localcoupon.couponservice.common.enums.CommonErrorCode;
import com.localcoupon.couponservice.common.exception.CommonException;
import com.localcoupon.couponservice.common.infra.RedisConstants;
import com.localcoupon.couponservice.common.util.PasswordEncoder;
import com.localcoupon.couponservice.common.util.TokenGenerator;
import com.localcoupon.couponservice.user.entity.User;
import com.localcoupon.couponservice.user.enums.UserErrorCode;
import com.localcoupon.couponservice.user.exception.UserNotFoundException;
import com.localcoupon.couponservice.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public LoginResponseDto login(LoginRequestDto request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UserNotFoundException(UserErrorCode.USER_NOT_FOUND));

        if (!PasswordEncoder.decrypt(user.getPasswordEnc(), request.password())) {
            throw new PasswordNotMatchException(AuthErrorCode.PASSWORD_NOT_MATCHING);
        }

        String sessionToken = TokenGenerator.createSessionToken();

        // UserSessionDto 생성
        UserSessionDto sessionDto = new UserSessionDto(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                List.of(user.getRole().name())
        );

        try {
            redisTemplate.opsForValue().set(
                    RedisConstants.SESSION_PREFIX + sessionToken,
                    objectMapper.writeValueAsString(sessionDto),
                    RedisConstants.SESSION_TTL
            );
        } catch (JsonProcessingException e) {
            throw new CommonException(CommonErrorCode.JSON_SERIALIZE_ERROR);
        }

        return LoginResponseDto.of(sessionToken);
    }

    @Override
    public LogoutResponseDto logout(String sessionToken) {
        redisTemplate.delete(RedisConstants.SESSION_PREFIX + sessionToken);
        return new LogoutResponseDto(sessionToken);
    }
}
