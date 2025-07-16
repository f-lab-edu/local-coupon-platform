package com.localcoupon.couponservice.auth.service;

import com.localcoupon.couponservice.auth.dto.UserSessionDto;
import com.localcoupon.couponservice.auth.dto.request.LoginRequestDto;
import com.localcoupon.couponservice.auth.dto.response.LoginResponseDto;
import com.localcoupon.couponservice.auth.exception.PasswordNotMatchException;
import com.localcoupon.couponservice.auth.repository.SessionRepository;
import com.localcoupon.couponservice.auth.service.impl.AuthServiceImpl;
import com.localcoupon.couponservice.common.util.PasswordEncoder;
import com.localcoupon.couponservice.user.dto.request.SignUpRequestDto;
import com.localcoupon.couponservice.user.entity.User;
import com.localcoupon.couponservice.user.exception.UserNotFoundException;
import com.localcoupon.couponservice.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AuthServiceTest {

    @Mock
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SessionRepository sessionRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authService = new AuthServiceImpl(userRepository, sessionRepository);

    }

    @Test
    @DisplayName("로그인 성공")
    void loginSuccess() throws Exception {
        // given
        String email = "test@example.com";
        String rawPassword = "password123";
        String address = "서울 송파구";
        String regionCode = "02146";
        Long userId = 1L;
        String nickname = "dong";

        //이메일로 유저조회 실행 시 given 사전 정의 데이터로 변환한다.
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(
                User.from(SignUpRequestDto.of
                        (email,rawPassword,nickname,address,regionCode))));

        //repository save도 true를 반환한다고 가정한다.
        when(sessionRepository.save(anyString(), any(UserSessionDto.class))).thenReturn(true);


        LoginRequestDto requestDto = new LoginRequestDto(email, rawPassword);

        // when
        LoginResponseDto response = authService.login(requestDto);

        // then
        assertThat(response.token()).isNotNull();
        verify(sessionRepository).save(
                anyString(),
                any(UserSessionDto.class));
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
    @DisplayName("유저가 로그인할때 비밀번호를 잘못 입력한다.")
    void 로그인실패_비밀번호불일치() {
        //given
        String email = "test@example.com";
        String correctPassword = "1234";
        String wrongPassword = "no";

        User user = User.builder()
                .email(email)
                .passwordEnc(PasswordEncoder.encrypt(correctPassword))
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        //when, then
        LoginRequestDto requestDto = new LoginRequestDto(email, wrongPassword);
        assertThrows(PasswordNotMatchException.class, () -> {
            authService.login(requestDto);
        });

    }

    @Test
    @DisplayName("로그아웃 시 Redis에서 세션 토큰을 삭제한다")
    void 로그아웃() {
        //given
        String sessionToken = "abcd-1234";

        //when
        authService.logout(sessionToken);

        //then
        verify(sessionRepository).delete(sessionToken);
    }
}
