package com.localcoupon.couponservice.auth.service;

import com.localcoupon.couponservice.auth.dto.request.LoginRequestDto;
import com.localcoupon.couponservice.auth.exception.PasswordNotMatchException;
import com.localcoupon.couponservice.auth.service.impl.AuthServiceImpl;
import com.localcoupon.couponservice.global.util.PasswordEncoder;
import com.localcoupon.couponservice.global.util.RedisUtils;
import com.localcoupon.couponservice.auth.dto.response.LoginResponseDto;
import com.localcoupon.couponservice.user.entity.User;
import com.localcoupon.couponservice.user.exception.UserNotFoundException;
import com.localcoupon.couponservice.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class AuthServiceTest {

    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        authService = new AuthServiceImpl(userRepository, redisTemplate);
    }

    @Test
    @DisplayName("로그인 성공")
    void loginSuccess() {
        //given
        String email = "test@example.com";
        String rawPassword = "password123";
        String hashedPassword = PasswordEncoder.encrypt(rawPassword);

        User user = User.builder()
                .email(email)
                .passwordEnc(hashedPassword)
                .build();
        //when
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        LoginRequestDto requestDto = new LoginRequestDto(email, rawPassword);
        LoginResponseDto response = authService.login(requestDto);

        //then
        assertThat(response.token()).isNotNull();
        verify(redisTemplate.opsForValue()).set(startsWith(RedisUtils.SESSION_PREFIX), eq(email), any(Duration.class));
    }

    @Test
    @DisplayName("로그인 실패 - 유저 없음")
    void 로그인실패_유저없음() {
        //given
        String email = "notfound@example.com";
        LoginRequestDto requestDto = new LoginRequestDto(email, "pass");

        //when
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        //then
        assertThatThrownBy(() -> authService.login(requestDto))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 불일치")
    void 로그인실패_비밀번호불일치() {
        //given
        String email = "test@example.com";
        String correctPassword = "1234";
        String wrongPassword = "no";

        User user = User.builder()
                .email(email)
                .passwordEnc(PasswordEncoder.encrypt(correctPassword))
                .build();

        //when
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        LoginRequestDto requestDto = new LoginRequestDto(email, wrongPassword);
        //then
        assertThatThrownBy(() -> authService.login(requestDto))
                .isInstanceOf(PasswordNotMatchException.class);
    }

    @Test
    @DisplayName("로그아웃 시 Redis에서 세션 토큰을 삭제한다")
    void 로그아웃() {
        //given
        String sessionToken = "abcd-1234";
        String redisKey = "SESSION:" + sessionToken;

        //when
        authService.logout(sessionToken);

        //then
        verify(redisTemplate).delete(redisKey); // 단순하게 한 번만 호출되는지 확인
    }
}
