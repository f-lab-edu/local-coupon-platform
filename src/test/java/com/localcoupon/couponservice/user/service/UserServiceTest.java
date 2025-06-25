package com.localcoupon.couponservice.user.service;

import com.localcoupon.couponservice.global.util.PasswordEncoder;
import com.localcoupon.couponservice.user.dto.request.SignUpRequestDto;
import com.localcoupon.couponservice.user.dto.response.UserResponseDto;
import com.localcoupon.couponservice.user.entity.User;
import com.localcoupon.couponservice.user.exception.UserAlreadyExistsException;
import com.localcoupon.couponservice.user.exception.UserNotFoundException;
import com.localcoupon.couponservice.user.repository.UserRepository;
import com.localcoupon.couponservice.user.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void 설정() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("회원가입 성공")
    void 회원가입_성공() {
        //given
        SignUpRequestDto dto = new SignUpRequestDto("test@example.com", "password123", "tester", "서울시", "11005");
        User mockSavedUser = User.from(dto);

        when(userRepository.existsByEmail(dto.email())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(mockSavedUser);

        //when
        UserResponseDto response = userService.signUpUser(dto);

        //then
        assertThat(response.email()).isEqualTo(dto.email());
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("회원가입 실패 - 중복 이메일")
    void 회원가입_실패_중복이메일() {
        SignUpRequestDto dto = new SignUpRequestDto("test@example.com", "password123", "tester", "서울시", "11005");

        when(userRepository.existsByEmail(dto.email())).thenReturn(true);

        assertThatThrownBy(() -> userService.signUpUser(dto))
                .isInstanceOf(UserAlreadyExistsException.class);
    }

    @Test
    @DisplayName("이메일로 유저 조회 성공")
    void 이메일로_유저조회_성공() {
        String email = "test@example.com";
        User user = User.builder().email(email).build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        UserResponseDto result = userService.getUserByEmail(email);
        assertThat(result.email()).isEqualTo(email);
    }

    @Test
    @DisplayName("이메일로 유저 조회 실패")
    void 이메일로_유저조회_실패() {
        String email = "notfound@example.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserByEmail(email))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    @DisplayName("회원가입 시 비밀번호 암호화 확인")
    void 회원가입시_비밀번호_암호화_확인() {
        // given
        SignUpRequestDto dto = new SignUpRequestDto("test@example.com", "password123", "tester", "서울시", "11005");
        when(userRepository.existsByEmail(dto.email())).thenReturn(false);

        User savedUser = User.from(dto);

        // when
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserResponseDto response = userService.signUpUser(dto);

        // then
        assertThat(PasswordEncoder.decrypt(savedUser.getPasswordEnc(), dto.password())).isTrue();
        verify(userRepository).save(any(User.class));
    }

}
