package com.localcoupon.couponservice.coupon.entity;

import com.localcoupon.couponservice.coupon.CouponData;
import com.localcoupon.couponservice.coupon.enums.UserCouponErrorCode;
import com.localcoupon.couponservice.coupon.exception.UserCouponException;
import org.aspectj.lang.annotation.After;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Clock;
import java.time.LocalDateTime;

class IssuedCouponTest {
    @Mock
    private IssuedCoupon issuedCoupon;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("사용자는 발급된 쿠폰을 사용한다.")
    IssuedCoupon use() {
    }

    @AfterEach
    void tearDown() {
    }
}