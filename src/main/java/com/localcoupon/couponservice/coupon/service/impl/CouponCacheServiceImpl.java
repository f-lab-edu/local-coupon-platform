package com.localcoupon.couponservice.coupon.service.impl;

import com.localcoupon.couponservice.coupon.entity.Coupon;
import com.localcoupon.couponservice.coupon.enums.CouponStock;
import com.localcoupon.couponservice.coupon.enums.UserCouponErrorCode;
import com.localcoupon.couponservice.coupon.exception.UserCouponException;
import com.localcoupon.couponservice.coupon.service.CouponCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.localcoupon.couponservice.common.infra.RedisConstants.COUPON_OPEN_PREFIX;

@Service
@RequiredArgsConstructor
public class CouponCacheServiceImpl implements CouponCacheService {

    private final RedisTemplate<String, Integer> redisTemplate;

    @Override
    public void saveCouponForOpen(Coupon coupon) {
        String key = COUPON_OPEN_PREFIX + coupon.getId();

        redisTemplate.opsForValue().set(key, coupon.getTotalCount());

        //TTL 길게 설정하여 배치 DB 동기화 시간을 충분히 잡아둔다.
        long ttl = Duration.between(
                LocalDateTime.now(),
                coupon.getCouponValidEndTime().plusDays(1)
        ).getSeconds();

        redisTemplate.expire(key, ttl, TimeUnit.SECONDS);
    }

    @Override
    public boolean isCouponOpen(Long couponId) {
        String key = COUPON_OPEN_PREFIX + couponId;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    @Override
    public boolean decreaseCouponStock(Long couponId) {
        String key = COUPON_OPEN_PREFIX + couponId;

        String script = """
            local stock = redis.call('DECR', KEYS[1])
            if stock < 0 then
                redis.call('INCR', KEYS[1])
                return 0
            else
                return 1
            end
        """;

        DefaultRedisScript<Integer> redisScript = new DefaultRedisScript<>(script, Integer.class);

        Integer result = redisTemplate.execute(redisScript, List.of(key));

        if (result == CouponStock.SOLD_OUT.getValue()) {
            throw new UserCouponException(UserCouponErrorCode.SOLD_OUT_COUPON);
        }

        return true;
    }
}
