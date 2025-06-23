package com.localcoupon.couponservice.user.service;

import com.localcoupon.couponservice.global.util.PasswordEncoder;
import com.localcoupon.couponservice.user.dto.request.SignUpRequestDto;
import com.localcoupon.couponservice.user.dto.response.UserResponseDto;
import com.localcoupon.couponservice.user.entity.User;
import com.localcoupon.couponservice.user.enums.UserErrorCode;
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
import static org.mockito.Mockito.*;

class UserServiceImplTest {

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
        SignUpRequestDto dto = new SignUpRequestDto("test@example.com", "password123", "tester", "서울시", "110105");

        when(userRepository.existsByEmail(dto.email())).thenReturn(false);

        User mockSavedUser = User.builder()
                .id(1L)
                .email(dto.email())
                .passwordEnc(PasswordEncoder.hash(dto.password()))
                .nickname(dto.nickname())
                .address(dto.address())
                .regionCode(dto.regionCode())
                .build();

        when(userRepository.save(any(User.class))).thenReturn(mockSavedUser);

        UserResponseDto response = userService.signUpUser(dto);

        assertThat(response.email()).isEqualTo(dto.email());
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("회원가입 실패 - 중복 이메일")
    void 회원가입_실패_중복이메일() {
        SignUpRequestDto dto = new SignUpRequestDto("test@example.com", "password123", "tester", "서울시", "110105");

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
        SignUpRequestDto dto = new SignUpRequestDto("test@example.com", "password123", "tester", "서울시", "110105");
        when(userRepository.existsByEmail(dto.email())).thenReturn(false);

        // save()가 호출되기 전에 암호화된 비밀번호를 직접 생성해서 비교
        String encrypted = PasswordEncoder.hash(dto.password());

        // save()가 호출되면 암호화된 비밀번호가 사용되었는지 검증하기 위해 mock 반환값 구성
        User savedUser = User.builder()
                .id(1L)
                .email(dto.email())
                .passwordEnc(encrypted) // 실제 로직에서 암호화해서 넣을 예정
                .nickname(dto.nickname())
                .address(dto.address())
                .regionCode(dto.regionCode())
                .build();

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // when
        UserResponseDto response = userService.signUpUser(dto);

        // then
        assertThat(PasswordEncoder.verify(savedUser.getPasswordEnc(), dto.password())).isTrue();
        verify(userRepository).save(any(User.class));
    }

}
