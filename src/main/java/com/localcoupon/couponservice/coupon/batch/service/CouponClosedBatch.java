package com.localcoupon.couponservice.coupon.batch.service;

import com.localcoupon.couponservice.common.enums.Result;
import com.localcoupon.couponservice.common.infra.RedisProperties;
import com.localcoupon.couponservice.coupon.entity.Coupon;
import com.localcoupon.couponservice.coupon.repository.CouponRedisRepository;
import com.localcoupon.couponservice.coupon.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class CouponClosedBatch {

    private final CouponRepository couponRepository;
    private final CouponRedisRepository couponRedisRepository;
    private final RedisProperties redisProperties;
    private final Clock clock;

    @Scheduled(cron = "0 0 */1 * * *")
    public void deleteExpiredCoupons() {
        LocalDateTime now = LocalDateTime.now(clock);

        couponRepository.findByCouponIssueEndTimeBefore(now)
                .forEach(coupon -> {
                    try {
                        syncIssuedCountToDb(coupon);
                        deleteRedisKey(coupon.getId());
                    } catch (Exception e) {
                        log.error("[Coupon-Closed-Batch] Failed processing couponId={}", coupon.getId(), e);
                    }
                });

    }

    private Result syncIssuedCountToDb(Coupon coupon) {
        String redisKey = redisProperties.couponOpenPrefix() + coupon.getId();

        return couponRedisRepository.getValue(redisKey, String.class)
                .map(Integer::parseInt)
                .map(issuedCount -> {
                    coupon.syncIssuedCount(issuedCount);

                    log.info("[Coupon-Closed-Batch] couponId={} , issuedCount={} → DB 업데이트",
                            coupon.getId(), issuedCount);
                    return Result.SUCCESS;
                })
                .orElse(Result.FAIL);
    }

    private boolean deleteRedisKey(Long couponId) {
        if (couponRedisRepository.deleteData(redisProperties.couponOpenPrefix() + couponId)) {
            log.info("[Coupon-Closed-Batch] Deleted Redis couponId: {}", couponId);
            return true;
        }
        return false;
    }
}
