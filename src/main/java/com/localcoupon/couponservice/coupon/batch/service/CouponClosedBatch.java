package com.localcoupon.couponservice.coupon.batch.service;

import com.localcoupon.couponservice.coupon.repository.CouponRedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.localcoupon.couponservice.common.infra.RedisConstants.COUPON_OPEN_PREFIX;

@Service
@Slf4j
@RequiredArgsConstructor
public class CouponClosedBatch {

    private final CouponRedisRepository couponRedisRepository;
    private final JdbcTemplate jdbcTemplate;

    @Scheduled(cron = "0 0 */1 * * *") // 1시간마다 실행
    public void deleteExpiredCoupons() {
        LocalDateTime now = LocalDateTime.now();

        List<Long> expiredCouponIds = jdbcTemplate.query(
                """
                SELECT id
                  FROM coupon
                 WHERE coupon_issue_end_time < ?
                   AND is_deleted = false
                """,
                (rs, rowNum) -> rs.getLong("id"),
                now
        );

        expiredCouponIds.forEach(couponId -> {
                    try {
                        String redisKey = COUPON_OPEN_PREFIX + couponId;
                        Optional<Long> issuedCountNullable = couponRedisRepository.getValue(redisKey);
                        // Redis 재고 동기화
                        issuedCountNullable.ifPresent(issuedCount -> {
                                    int updatedRows = jdbcTemplate.update(
                                            """
                                            UPDATE coupon
                                               SET coupon_issued_count = ?
                                             WHERE id = ?
                                            """,
                                            issuedCount,
                                            couponId
                                    );

                                    log.info("[Coupon-Closed-Batch] couponId={} , issuedCount={} → DB 업데이트 ({} rows)",
                                            couponId, issuedCount, updatedRows
                                    );
                                });

                        // Redis 키 삭제
                        couponRedisRepository.deleteData(redisKey);

                        log.info("[Coupon-Closed-Batch] Deleted Redis key: {}", redisKey);
                    } catch (Exception e) {
                        log.error("[Coupon-Closed-Batch] Failed to delete Redis key for couponId={}", couponId, e);
                    }
                });


        expiredCouponIds.forEach(expiredCouponId -> {
                        couponRedisRepository.deleteData(COUPON_OPEN_PREFIX + expiredCouponId);
                        log.info("[Coupon-Closed-Batch] Deleted Redis key: {}", expiredCouponId);
                });

    }
}

