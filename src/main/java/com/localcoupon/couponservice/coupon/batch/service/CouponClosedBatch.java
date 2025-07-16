package com.localcoupon.couponservice.coupon.batch.service;

import com.localcoupon.couponservice.common.infra.RedisProperties;
import com.localcoupon.couponservice.coupon.batch.mapper.CouponBatchMapper;
import com.localcoupon.couponservice.coupon.repository.CouponRedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CouponClosedBatch {

    private final CouponRedisRepository couponRedisRepository;
    private final CouponBatchMapper couponBatchMapper;
    private final RedisProperties redisProperties;

    @Scheduled(cron = "0 0 */1 * * *") // 1시간마다 실행
    public void deleteExpiredCoupons() {
        LocalDateTime now = LocalDateTime.now();

        List<Long> expiredCouponIds = couponBatchMapper.findExpiredCouponIds(now);

        expiredCouponIds.forEach(couponId -> {
            try {
                syncIssuedCountToDb(couponId);
                deleteRedisKey(couponId);
            } catch (Exception e) {
                log.error("[Coupon-Closed-Batch] Failed processing couponId={}", couponId, e);
            }
        });
    }

    private void syncIssuedCountToDb(Long couponId) {
        String redisKey = redisProperties.couponOpenPrefix() + couponId;

        couponRedisRepository.getValue(redisKey, String.class)
                .map(Long::parseLong)
                .ifPresentOrElse(
                        issuedCount -> {
                            int updatedRows = couponBatchMapper.updateIssuedCount(couponId, issuedCount);
                            log.info("[Coupon-Closed-Batch] couponId={} , issuedCount={} → DB 업데이트 ({} rows)",
                                    couponId, issuedCount, updatedRows);
                        },
                        () -> log.info("[Coupon-Closed-Batch] No issuedCount found in Redis for couponId={}", couponId)
                );
    }


        private boolean deleteRedisKey(Long couponId) {
            if (couponRedisRepository.deleteData(redisProperties.couponOpenPrefix() + couponId)) {
                log.info("[Coupon-Closed-Batch] Deleted Redis couponId: {}", couponId);
                return true;
            }
            return false;
    }
}
