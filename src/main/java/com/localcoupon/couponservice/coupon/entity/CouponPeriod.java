package com.localcoupon.couponservice.coupon.entity;

import com.localcoupon.couponservice.coupon.annotation.ValidPeriod;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ValidPeriod
public class CouponPeriod {

    @Column(name = "start_time")
    private LocalDateTime start;

    @Column(name = "end_time")
    private LocalDateTime end;

    public CouponPeriod(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null || end.isBefore(start)) {
            throw new IllegalArgumentException("쿠폰 기간이 잘못되었습니다.");
        }
        this.start = start;
        this.end = end;
    }

    public boolean isExpired(LocalDateTime now) {
        return now.isAfter(end);
    }
}
