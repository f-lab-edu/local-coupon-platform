package com.localcoupon.couponservice.coupon.entity;

import com.localcoupon.couponservice.coupon.CouponData;
import com.localcoupon.couponservice.coupon.dto.request.CouponUpdateRequestDto;
import com.localcoupon.couponservice.coupon.enums.CouponScope;
import com.localcoupon.couponservice.coupon.exception.UserCouponException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Clock;

public class CouponTest {
    private Coupon testCoupon;
    @BeforeEach
    void setUp() {
        testCoupon = CouponData.activeCoupon(1L, Clock.systemDefaultZone());
    }
    @Test
    @DisplayName("쿠폰 재고를 정상으로 업데이트한다.")
    void 재고업데이트_테스트() {
        //given
        Coupon coupon = testCoupon;
        //when
        Coupon updatedCoupon = coupon.syncIssuedCount(20);
        //then
        Assertions.assertEquals(20, updatedCoupon.getIssuedCount());
    }

    @Test
    @DisplayName("쿠폰 재고는 음수가 될 수 없다.")
    void 쿠폰재고_음수불가_테스트() {
        //given
        Coupon coupon = testCoupon;
        //when
        //then
        Assertions.assertThrows(UserCouponException.class, () -> coupon.syncIssuedCount(-1));
    }

    @Test
    @DisplayName("정보가 들어있는 값으로 쿠폰을 업데이트한다.")
    void 쿠폰정상_업데이트_테스트() {
        //given
        Coupon coupon = testCoupon;
        CouponUpdateRequestDto updateRequestDto = new CouponUpdateRequestDto("업데이트", "업데이트123", CouponScope.NATIONAL, 100,null,null);
        //when
        Coupon updatedCoupon = coupon.update(updateRequestDto);
        //then
        Assertions.assertEquals("업데이트", updatedCoupon.getTitle());
        Assertions.assertEquals(100, updatedCoupon.getTotalCount());
        Assertions.assertEquals(coupon.getIssuePeriod(), updatedCoupon.getIssuePeriod());
    }
}
