package com.localcoupon.couponservice.coupon.service.impl;

import com.localcoupon.couponservice.coupon.entity.Coupon;
import com.localcoupon.couponservice.coupon.enums.UserCouponErrorCode;
import com.localcoupon.couponservice.coupon.exception.UserCouponException;
import com.localcoupon.couponservice.coupon.repository.CouponRedisRepository;
import com.localcoupon.couponservice.coupon.service.CouponCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

import static com.localcoupon.couponservice.common.infra.RedisConstants.COUPON_LOCK_PREFIX;
import static com.localcoupon.couponservice.common.infra.RedisConstants.COUPON_OPEN_PREFIX;

@Service
@RequiredArgsConstructor
public class CouponRedissonServiceImpl implements CouponCacheService {

    private final CouponRedisRepository couponRedisRepository;

    @Override
    public Coupon saveCouponForOpen(Coupon coupon) {
        String key = COUPON_OPEN_PREFIX + coupon.getId();

        long ttlSeconds = Duration.between(
                LocalDateTime.now(),
                coupon.getCouponValidEndTime().plusDays(1)
        ).getSeconds();

        couponRedisRepository.saveData(
                key,
                coupon.getTotalCount(),
                Duration.ofSeconds(ttlSeconds)
        );
        return coupon;
    }

    @Override
    public boolean isCouponOpen(Long couponId) {
        String key = COUPON_OPEN_PREFIX + couponId;
        return couponRedisRepository.exists(key);
    }

    @Override
    public int decreaseCouponStock(Long couponId) {
        String lockKey = COUPON_LOCK_PREFIX + couponId;
        String dataKey = COUPON_OPEN_PREFIX + couponId;

        return couponRedisRepository.executeWithLock(
                lockKey,
                3,
                3,
                () -> couponRedisRepository.decreaseCouponStock(dataKey)
                        .orElseThrow(() -> new UserCouponException(UserCouponErrorCode.SOLD_OUT_COUPON))
        );
    }
}
