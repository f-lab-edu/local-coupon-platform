package com.localcoupon.couponservice.coupon;

import com.localcoupon.couponservice.coupon.entity.Coupon;
import com.localcoupon.couponservice.coupon.entity.CouponPeriod;
import com.localcoupon.couponservice.coupon.enums.CouponScope;
import com.localcoupon.couponservice.store.StoreData;

import java.time.Clock;
import java.time.LocalDateTime;

public class CouponData {

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
                .store(StoreData.defaultStore())
                .build();
    }

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
                .issuePeriod(new CouponPeriod(now.minusDays(1), now.minusDays(5)))
                .store(StoreData.defaultStore())
                .build();
    }
}

