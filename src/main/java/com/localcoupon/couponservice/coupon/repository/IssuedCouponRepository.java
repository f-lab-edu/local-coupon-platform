package com.localcoupon.couponservice.coupon.repository;

import com.localcoupon.couponservice.coupon.entity.IssuedCoupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IssuedCouponRepository extends JpaRepository<IssuedCoupon, Long> {
    Optional<IssuedCoupon> findByQrToken(String qrToken);
}
