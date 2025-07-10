package com.localcoupon.couponservice.coupon.batch.service;

import com.localcoupon.couponservice.coupon.enums.UserCouponErrorCode;
import com.localcoupon.couponservice.coupon.exception.UserCouponException;
import com.localcoupon.couponservice.coupon.repository.CouponRedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CouponStockSyncBatch {
    private final JdbcTemplate jdbcTemplate;
    private final CouponRedisRepository couponRedisRepository;

    @Scheduled(cron = "0 */1 * * * *") // 1분마다 동기화
    public void syncIssuedCount() {
        // 모든 coupon:open:issuedCount key 찾기
        couponRedisRepository.getAllOpenCouponKeys().forEach(this::syncSingleCoupon);
    }

    private void syncSingleCoupon(String redisKey) {
        // key → couponId 추출
        Long couponId = extractCouponId(redisKey);
        // Optional<Long>로 타입 맞춰주기
        Optional<Long> issuedCountNullable = couponRedisRepository.getValue(redisKey);

        issuedCountNullable.ifPresentOrElse(issuedCount -> {
                    int updatedRows = jdbcTemplate.update(
                            """
                            UPDATE coupon
                               SET coupon_issued_count = ?
                             WHERE id = ?
                            """,
                            issuedCount,
                            couponId
                    );
                    log.info("[Coupon-Sync-Batch] couponId={} , issuedCount={} → DB 업데이트 ({} rows)",
                            couponId, issuedCount, updatedRows);
                    },
                () ->  log.info("[Coupon-Sync-Batch] No coupon found for couponId={}", couponId)
        );
    }

    private Long extractCouponId(String key) {
        // coupon:open:{couponId} - issuedCount
        return Optional.of(List.of(key.split(":")))
                .filter(list -> list.size() == 3)
                .map(list -> Long.parseLong(list.get(2)))
                .orElseThrow(() -> new UserCouponException(UserCouponErrorCode.COUPON_KEY_PARSING_FAILED));
    }
}
