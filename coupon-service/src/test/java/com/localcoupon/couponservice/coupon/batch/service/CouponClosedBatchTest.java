package com.localcoupon.couponservice.coupon.batch.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.localcoupon.couponservice.coupon.entity.Coupon;
import com.localcoupon.couponservice.coupon.fixture.CouponFixture;
import com.localcoupon.couponservice.coupon.repository.CouponRedisRepository;
import com.localcoupon.couponservice.coupon.repository.CouponRepository;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class CouponClosedBatchTest {

  Clock fixedClock;

  @Mock
  private CouponRedisRepository couponRedisRepository;

  @Mock
  private CouponRepository couponRepository;

  @InjectMocks
  private CouponClosedBatch couponClosedBatch;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    fixedClock = Clock.fixed(
        LocalDateTime.of(2025, 7, 16, 10, 0)
            .atZone(ZoneId.systemDefault())
            .toInstant(),
        ZoneId.systemDefault()
    );
    couponClosedBatch = new CouponClosedBatch(couponRepository, couponRedisRepository, fixedClock);
  }

  @Test
  @DisplayName("배치에서 쿠폰 오픈 타임 종료 시 정상 삭제처리한다")
  void deleteExpiredCoupons() {
    // given
    List<Coupon> expiredCoupons = List.of(CouponFixture.expiredCoupon(1L, fixedClock));

    given(couponRepository.findByCouponIssueEndTimeBefore(any()))
        .willReturn(expiredCoupons);

    given(couponRedisRepository.getValue(anyString(), eq(String.class)))
        .willReturn(Optional.of("10"));

    given(couponRedisRepository.deleteData(anyString()))
        .willReturn(true);

    // when
    couponClosedBatch.deleteExpiredCoupons();

    // then
    for (Coupon coupon : expiredCoupons) {
      Coupon expiredCoupon = coupon.syncIssuedCount(3);
      assertThat(coupon.getIssuedCount()).isEqualTo(expiredCoupon.getIssuedCount());
    }
    verify(couponRedisRepository, times(1)).deleteData(anyString());
  }

  @Test
  @DisplayName("하나의 쿠폰 처리 중 예외가 발생해도 다음 쿠폰을 계속 처리한다")
  void deleteExpiredCoupons_exceptionHandling() {
    // given
    List<Coupon> coupons = List.of(
        CouponFixture.expiredCoupon(1L, fixedClock),
        CouponFixture.expiredCoupon(2L, fixedClock)
    );

    given(couponRepository.findByCouponIssueEndTimeBefore(any()))
        .willReturn(coupons);

    // 1번 쿠폰 레디스 에러
    given(couponRedisRepository.getValue("coupon:open:" + 1L, String.class))
        .willThrow(new RuntimeException("Redis error"));

    // 2번 쿠폰 가져오기 성공
    given(couponRedisRepository.getValue("coupon:open:" + 2L, String.class))
        .willReturn(Optional.of("10"));

    given(couponRedisRepository.deleteData(anyString()))
        .willReturn(true);

    // when
    couponClosedBatch.deleteExpiredCoupons();

    // then
    verify(couponRedisRepository, times(2))
        .getValue(anyString(), eq(String.class));
    verify(couponRedisRepository, times(1))
        .deleteData("coupon:open:" + 2L);
  }
}
