package com.localcoupon.couponservice.coupon.repository;

import com.localcoupon.couponservice.coupon.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long>, CouponRepositoryCustom {
    @Query("SELECT c FROM Coupon c WHERE c.issuePeriod.end < :now")
    List<Coupon> findByCouponIssueEndTimeBefore(LocalDateTime now);
}
