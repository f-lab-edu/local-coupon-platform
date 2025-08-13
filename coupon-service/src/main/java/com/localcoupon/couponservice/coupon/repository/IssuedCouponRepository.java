package com.localcoupon.couponservice.coupon.repository;

import com.localcoupon.couponservice.coupon.entity.IssuedCoupon;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IssuedCouponRepository extends JpaRepository<IssuedCoupon, Long> {

  Optional<IssuedCoupon> findByQrToken(String qrToken);
  
  boolean existsByCouponIdAndUserId(Long couponId, Long userId);
}
