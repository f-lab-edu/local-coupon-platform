package com.localcoupon.couponservice.coupon.batch.service;

import com.localcoupon.couponservice.coupon.batch.mapper.CouponBatchMapper;
import com.localcoupon.couponservice.coupon.enums.UserCouponErrorCode;
import com.localcoupon.couponservice.coupon.exception.UserCouponException;
import com.localcoupon.couponservice.coupon.repository.CouponRedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class CouponStockSyncBatch {

    private final CouponRedisRepository couponRedisRepository;
    private final CouponBatchMapper couponBatchMapper;

    @Scheduled(cron = "0 */1 * * * *") // 1분마다 동기화
    public void syncIssuedCount() {
        couponRedisRepository.getAllOpenCouponKeys().forEach(this::syncSingleCoupon);
    }

    private void syncSingleCoupon(String redisKey) {
        Long couponId = extractCouponId(redisKey);

        couponRedisRepository.getValue(redisKey, String.class)
                .map(Long::parseLong)
                .ifPresentOrElse(
                        issuedCount -> {
                            // coupon_total_count - issuedCount 로 업데이트
                            int updatedRows = couponBatchMapper.updateIssuedCount(couponId, issuedCount);

                            log.info("[Coupon-Sync-Batch] couponId={} , issuedCount={} → DB 업데이트 ({} rows)",
                                    couponId, issuedCount, updatedRows);
                        },
                        () -> log.info("[Coupon-Sync-Batch] No coupon found for couponId={}", couponId)
                );
    }

    private Long extractCouponId(String key) {
        return Optional.of(List.of(key.split(":")))
                .filter(list -> list.size() == 3)
                .map(list -> Long.parseLong(list.get(2)))
                .orElseThrow(() -> new UserCouponException(UserCouponErrorCode.COUPON_KEY_PARSING_FAILED));
    }
}
