package com.localcoupon.couponservice.coupon.entity;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class IssuedCouponTest {
    @Mock
    private IssuedCoupon issuedCoupon;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @AfterEach
    void tearDown() {
    }
}