package com.localcoupon.couponservice.coupon.service.impl;

import com.localcoupon.couponservice.common.infra.RedisProperties;
import com.localcoupon.couponservice.coupon.entity.Coupon;
import com.localcoupon.couponservice.coupon.enums.UserCouponErrorCode;
import com.localcoupon.couponservice.coupon.exception.UserCouponException;
import com.localcoupon.couponservice.coupon.repository.CouponRedisRepository;
import com.localcoupon.couponservice.coupon.service.CouponCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
@Primary
public class CouponRedissonServiceImpl implements CouponCacheService {

    private final CouponRedisRepository couponRedisRepository;
    private final RedisProperties redisProperties;

    @Override
    public Coupon saveCouponForOpen(Coupon coupon) {
        String key = redisProperties.couponOpenPrefix() + coupon.getId();

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
        String key = redisProperties.couponOpenPrefix() + couponId;
        return couponRedisRepository.exists(key);
    }

    @Override
    public int decreaseCouponStock(Long couponId) {
        String lockKey = redisProperties.couponLockPrefix() + couponId;
        String dataKey = redisProperties.couponOpenPrefix() + couponId;

        return couponRedisRepository.executeWithLock(
                lockKey,
                2000,
                5000,
                () -> couponRedisRepository.decreaseCouponStock(dataKey)
                        .orElseThrow(() -> new UserCouponException(UserCouponErrorCode.SOLD_OUT_COUPON))
        );
    }
}
