package com.localcoupon.couponservice.coupon.repository;

import com.localcoupon.couponservice.coupon.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long>, CouponRepositoryCustom {
    List<Coupon> findByCouponIssueEndTimeBefore(LocalDateTime now);
}
