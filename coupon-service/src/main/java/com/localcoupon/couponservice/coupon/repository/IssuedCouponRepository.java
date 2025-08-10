package com.localcoupon.couponservice.coupon.repository;

import com.localcoupon.couponservice.coupon.entity.IssuedCoupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface IssuedCouponRepository extends JpaRepository<IssuedCoupon, Long> {
    Optional<IssuedCoupon> findByQrToken(String qrToken);
    @Query(value = """
    SELECT EXISTS(
        SELECT 1
        FROM issued_coupon
        WHERE coupon_id = :couponId AND user_id = :userId
    )
    """, nativeQuery = true)
    boolean existsIssuedCouponByCouponIdAndUserId(@Param("couponId") Long couponId,
                                                  @Param("userId") Long userId);
}
