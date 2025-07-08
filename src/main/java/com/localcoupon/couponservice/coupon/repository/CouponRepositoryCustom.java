package com.localcoupon.couponservice.coupon.repository;

import com.localcoupon.couponservice.common.dto.request.CursorPageRequest;
import com.localcoupon.couponservice.coupon.entity.Coupon;

import java.util.List;

/*
JpaRepository에 메서드 파싱 기능이 아닌 커스텀 메서드 추가
 */
public interface CouponRepositoryCustom {
    List<Coupon> findAllByOwnerIdWithCursorPaging(Long ownerId, CursorPageRequest request);
    // 스프링 Data 공식 문서 “If you want to add custom methods to a repository, define your own interface and provide an implementation.”
}