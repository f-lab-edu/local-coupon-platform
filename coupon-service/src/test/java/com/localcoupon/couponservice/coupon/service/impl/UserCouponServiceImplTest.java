package com.localcoupon.couponservice.coupon.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.localcoupon.common.enums.Result;
import com.localcoupon.couponservice.coupon.dto.response.UserIssuedCouponResponseDto;
import com.localcoupon.couponservice.coupon.entity.Coupon;
import com.localcoupon.couponservice.coupon.enums.UserCouponErrorCode;
import com.localcoupon.couponservice.coupon.exception.UserCouponException;
import com.localcoupon.couponservice.coupon.fixture.CouponFixture;
import com.localcoupon.couponservice.coupon.repository.CouponRepository;
import com.localcoupon.couponservice.coupon.repository.IssuedCouponRepository;
import com.localcoupon.couponservice.coupon.service.CouponIssueService;
import java.time.Clock;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserCouponServiceImplTest {

  private final Long userId = 100L;
  private final Long couponId = 1L;
  private final String userEmail = "user@test.com";
  @Mock
  private CouponIssueService couponIssueService;
  @Mock
  private CouponRepository couponRepository;
  @Mock
  private IssuedCouponRepository issuedCouponRepository;
  @InjectMocks
  private UserCouponServiceImpl userCouponService;

  // ---------- getUserCoupons ----------
  @Test
  @DisplayName("내 쿠폰 목록 조회 - 현재 구현은 빈 리스트 반환")
  void getUserCoupons_returnsEmptyList_now() {
    List<UserIssuedCouponResponseDto> result = userCouponService.getUserCoupons();
    assertThat(result).isEmpty();
    // 구현 추가되면 여기서 repository 호출/매핑 검증을 확장하세요.
  }

  // ---------- issueCoupon ----------
  @Test
  @DisplayName("쿠폰 발급 - 쿠폰이 존재하지 않으면 예외 발생")
  void issueCoupon_throws_whenCouponNotFound() {
    given(couponRepository.findById(couponId)).willReturn(Optional.empty());

    UserCouponException ex = assertThrows(
        UserCouponException.class,
        () -> userCouponService.issueCoupon(userId, couponId, userEmail)
    );
    assertThat(ex.getErrorCode()).isEqualTo(UserCouponErrorCode.COUPON_NOT_FOUND);

    verify(couponRepository).findById(couponId);
    verifyNoMoreInteractions(couponRepository, issuedCouponRepository, couponIssueService);
  }

  @Test
  @DisplayName("쿠폰 발급 - 이미 발급된 경우 FAIL 반환 및 비즈니스 로직 미실행")
  void issueCoupon_returnsFail_whenAlreadyIssued() {
    Coupon coupon = CouponFixture.activeCoupon(couponId, Clock.systemDefaultZone());

    given(couponRepository.findById(couponId)).willReturn(Optional.of(coupon));
    given(issuedCouponRepository.existsByCouponIdAndUserId(couponId, userId)).willReturn(true);

    Result result = userCouponService.issueCoupon(userId, couponId, userEmail);

    assertThat(result).isEqualTo(Result.FAIL);
    verify(couponRepository).findById(couponId);
    verify(issuedCouponRepository).existsByCouponIdAndUserId(couponId, userId);
    verifyNoInteractions(couponIssueService); // 중복이면 비즈니스 처리 안 함
  }

  @Test
  @DisplayName("쿠폰 발급 - 정상 발급 시 결과 반환 (SUCCESS/FAIL 등은 내부 로직에 따름)")
  void issueCoupon_callsBusinessProcess_whenNotDuplicated() {
    Coupon coupon = CouponFixture.activeCoupon(couponId, Clock.systemDefaultZone());

    given(couponRepository.findById(couponId)).willReturn(Optional.of(coupon));
    given(issuedCouponRepository.existsByCouponIdAndUserId(couponId, userId)).willReturn(false);
    given(couponIssueService.processCouponIssue(coupon, userId, userEmail)).willReturn(
        Result.SUCCESS);

    Result result = userCouponService.issueCoupon(userId, couponId, userEmail);

    assertThat(result).isEqualTo(Result.SUCCESS);
    verify(couponRepository).findById(couponId);
    verify(issuedCouponRepository).existsByCouponIdAndUserId(couponId, userId);
    verify(couponIssueService).processCouponIssue(coupon, userId, userEmail);
  }
}
