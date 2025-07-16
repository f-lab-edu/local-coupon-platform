package com.localcoupon.couponservice.coupon.batch.mapper;

import java.time.LocalDateTime;
import java.util.List;

public interface CouponBatchMapper {
    List<Long> findExpiredCouponIds(LocalDateTime now);

    int updateIssuedCount(Long couponId, Long issuedCount);
}
