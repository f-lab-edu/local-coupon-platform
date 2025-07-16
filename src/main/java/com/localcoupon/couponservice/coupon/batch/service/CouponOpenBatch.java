package com.localcoupon.couponservice.coupon.batch.service;

import com.localcoupon.couponservice.common.infra.RedisProperties;
import com.localcoupon.couponservice.coupon.batch.dto.CouponOpenDto;
import com.localcoupon.couponservice.coupon.repository.CouponRedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CouponOpenBatch {

    private final CouponRedisRepository couponRedisRepository;
    private final JdbcTemplate jdbcTemplate;
    private final RedisProperties redisProperties;

    @Scheduled(cron = "0 */5 * * * *") // 5분마다 실행
    public void openCoupons() {
        log.info("[Coupon-Open-Batch] 쿠폰 오픈 배치 시작");
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime tenMinLater = now.plusMinutes(10);

        List<CouponOpenDto> coupons = jdbcTemplate.query(
                """
                SELECT id, coupon_total_count, coupon_issue_end_time
                FROM coupon
                WHERE coupon_issue_start_time >= ?
                  AND coupon_issue_start_time <= ?
                  AND is_deleted = false
                """,
                (rs, rowNum) -> mapRowToCouponDto(rs),
                now,
                tenMinLater
        );

        Optional.of(coupons)
                .filter(list -> !list.isEmpty())
                .ifPresent(list -> list.forEach(this::openSingleCoupon));
    }

    private void openSingleCoupon(CouponOpenDto coupon) {
        String issuedCountKey = redisProperties.couponOpenPrefix() + coupon.id();

        if (couponRedisRepository.exists(issuedCountKey)) {
            log.info("[Coupon-Open-Batch] couponId={} already opened in Redis.", coupon.id());
            return;
        }

        couponRedisRepository.saveData(issuedCountKey,String.valueOf(coupon.couponLimit()), getTtlSeconds(coupon.couponIssueEndTime()));

        log.info("[Coupon-Open-Batch] couponId={} 등록 완료. totalCount={}", coupon.id(), coupon.couponLimit());
    }

    private Duration getTtlSeconds(LocalDateTime couponIssueEndTime) {
        return Duration.between(
                LocalDateTime.now(),
                couponIssueEndTime.plusDays(1)
        );
    }

    private CouponOpenDto mapRowToCouponDto(ResultSet rs) throws SQLException {
        return CouponOpenDto.of(
                rs.getLong("id"),
                rs.getLong("coupon_total_count"),
                rs.getTimestamp("coupon_issue_end_time").toLocalDateTime()
        );
    }
}

