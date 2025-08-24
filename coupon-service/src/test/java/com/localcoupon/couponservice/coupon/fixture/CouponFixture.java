package com.localcoupon.couponservice.coupon.fixture;

import com.localcoupon.couponservice.coupon.entity.Coupon;
import com.localcoupon.couponservice.coupon.entity.CouponPeriod;
import com.localcoupon.couponservice.coupon.enums.CouponScope;
import java.time.Clock;
import java.time.LocalDateTime;

public final class CouponFixture {

  private CouponFixture() {
  }

  /**
   * 기본 진행중 쿠폰 (now 기준 유효/발급 기간 진행중)
   */
  public static Coupon activeCoupon(Long id, Clock clock) {
    LocalDateTime now = LocalDateTime.now(clock);
    return Coupon.builder()
        .id(id)
        .scope(CouponScope.LOCAL)
        .title("진행중 쿠폰")
        .description("설명")
        .totalCount(100)
        .issuedCount(10)
        .validPeriod(new CouponPeriod(now.minusDays(1), now.plusDays(10)))
        .issuePeriod(new CouponPeriod(now.minusDays(1), now.plusDays(5)))
        .storeId(1L)
        .build();
  }

  /**
   * 기본 만료 쿠폰 (now 기준 유효/발급 기간 모두 과거)
   */
  public static Coupon expiredCoupon(Long id, Clock clock) {
    LocalDateTime now = LocalDateTime.now(clock);
    return Coupon.builder()
        .id(id)
        .scope(CouponScope.LOCAL)
        .title("테스트 쿠폰")
        .description("설명")
        .totalCount(100)
        .issuedCount(0)
        .validPeriod(new CouponPeriod(now.minusDays(10), now.minusDays(1)))
        .issuePeriod(new CouponPeriod(now.minusDays(10), now.minusDays(1)))
        .storeId(1L)
        .build();
  }
}
