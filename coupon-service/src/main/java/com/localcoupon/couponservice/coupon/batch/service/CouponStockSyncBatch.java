package com.localcoupon.couponservice.coupon.batch.service;

import com.localcoupon.couponservice.coupon.entity.Coupon;
import com.localcoupon.couponservice.coupon.enums.CouponStock;
import com.localcoupon.couponservice.coupon.enums.UserCouponErrorCode;
import com.localcoupon.couponservice.coupon.exception.UserCouponException;
import com.localcoupon.couponservice.coupon.repository.CouponRedisRepository;
import com.localcoupon.couponservice.coupon.repository.CouponRepository;
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
    private final CouponRepository couponRepository;

    @Scheduled(cron = "0 */1 * * * *") // 1분마다 동기화
    public void syncIssuedCount() {
        couponRedisRepository.getAllOpenCouponKeys().forEach(this::syncSingleCoupon);
    }

    @Transactional
    public int syncSingleCoupon(String redisKey) {
        Long couponId = extractCouponId(redisKey);

        Optional<String> redisValue = couponRedisRepository.getValue(redisKey, String.class);
        if (redisValue.isEmpty()) {
            log.info("[Coupon-Sync-Batch] No value found in Redis for key={}", redisKey);
            return CouponStock.SOLD_OUT.getValue();
        }

        Long issuedCount = Long.parseLong(redisValue.get());
        return couponRepository.findById(couponId)
                .map(coupon -> {
                    Coupon stockUpdatedCoupon = coupon.syncIssuedCount(issuedCount.intValue());
                    couponRepository.save(stockUpdatedCoupon); // 갱신된 엔티티 저장
                    log.info("[Coupon-Sync-Batch] couponId={} , issuedCount={} → DB 업데이트 완료",
                            couponId, issuedCount);
                    return stockUpdatedCoupon.getIssuedCount();
                })
                .orElseGet(() -> {
                    log.info("[Coupon-Sync-Batch] No coupon found in DB for couponId={}", couponId);
                    return CouponStock.SOLD_OUT.getValue();
                });
    }

    private Long extractCouponId(String key) {
        return Optional.of(List.of(key.split(":")))
                .filter(list -> list.size() == 3)
                .filter(list -> list.get(1).equals("open"))
                .map(list -> Long.parseLong(list.get(2)))
                .orElseThrow(() -> new UserCouponException(UserCouponErrorCode.COUPON_KEY_PARSING_FAILED));
    }
}
