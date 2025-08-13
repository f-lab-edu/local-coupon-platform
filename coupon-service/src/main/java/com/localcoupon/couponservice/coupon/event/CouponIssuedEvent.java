package com.localcoupon.couponservice.coupon.event;

import java.time.LocalDateTime;

public record CouponIssuedEvent(
    Long issuedCouponId,
    String userEmail,
    LocalDateTime validStart,
    LocalDateTime validEnd,
    String couponTitle
) {

  public static CouponIssuedEvent of(Long issuedCouponId, String userEmail,
      LocalDateTime validStart, LocalDateTime validEnd, String couponTitle) {
    return new CouponIssuedEvent(issuedCouponId, userEmail, validStart, validEnd, couponTitle);
  }
}
