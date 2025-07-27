package com.localcoupon.couponservice.coupon.entity;

import com.localcoupon.couponservice.coupon.enums.UserCouponErrorCode;
import com.localcoupon.couponservice.coupon.exception.UserCouponException;
import jakarta.persistence.Embeddable;

import java.time.LocalDateTime;

@Embeddable
public record CouponPeriod(LocalDateTime start, LocalDateTime end) {
    public CouponPeriod {
        if (start == null || end == null) {
            throw new UserCouponException(UserCouponErrorCode.COUPON_VALIDATION_PREIOD_FAILED);
        }
        if (end.isBefore(start)) {
            throw new UserCouponException(UserCouponErrorCode.COUPON_VALIDATION_PREIOD_FAILED);
        }
    }

    // JPA용 no-args 생성자를 전체 생성자로 위임한다. (임시값)
    public CouponPeriod() {
        this(LocalDateTime.MIN, LocalDateTime.MIN);
    }
}
