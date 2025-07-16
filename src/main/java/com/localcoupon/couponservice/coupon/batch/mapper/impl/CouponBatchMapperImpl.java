package com.localcoupon.couponservice.coupon.batch.mapper.impl;

import com.localcoupon.couponservice.coupon.batch.mapper.CouponBatchMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class CouponBatchMapperImpl implements CouponBatchMapper {

    private final JdbcTemplate jdbcTemplate;

    private static final String FIND_EXPIRED_COUPONS_SQL = """
            SELECT id
              FROM coupon
             WHERE coupon_issue_end_time < ?
               AND is_deleted = false
            """;

    private static final String UPDATE_ISSUED_COUNT_SQL = """
            UPDATE coupon
               SET coupon_issued_count = ?
             WHERE id = ?
            """;

    @Override
    public List<Long> findExpiredCouponIds(LocalDateTime now) {
        return jdbcTemplate.query(
                FIND_EXPIRED_COUPONS_SQL,
                (rs, rowNum) -> rs.getLong("id"),
                now
        );
    }

    @Override
    public int updateIssuedCount(Long couponId, Long issuedCount) {
        return jdbcTemplate.update(
                UPDATE_ISSUED_COUNT_SQL,
                issuedCount,
                couponId
        );
    }
}
