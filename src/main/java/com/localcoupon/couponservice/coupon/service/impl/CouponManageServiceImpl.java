package com.localcoupon.couponservice.coupon.service.impl;

import com.localcoupon.couponservice.coupon.dto.request.CouponCreateRequestDto;
import com.localcoupon.couponservice.coupon.dto.response.CouponResponseDto;
import com.localcoupon.couponservice.coupon.dto.response.CouponVerifyResponseDto;
import com.localcoupon.couponservice.coupon.entity.Coupon;
import com.localcoupon.couponservice.coupon.repository.CouponRepository;
import com.localcoupon.couponservice.coupon.service.CouponManageService;
import com.localcoupon.couponservice.store.entity.Store;
import com.localcoupon.couponservice.store.repository.StoreRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CouponManageServiceImpl implements CouponManageService {
    private final CouponRepository couponRepository;
    private final StoreRepository storeRepository;

    @Override
    public CouponResponseDto createCoupon(CouponCreateRequestDto request, Long userId) {
        // 1. Store 조회
        Store store = storeRepository.findByOwnerId(userId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Store not found. id=" + userId)
                );

        // 2. Coupon entity 생성
        Coupon coupon = Coupon.from(request);

        // 3. 저장
        Coupon savedCoupon = couponRepository.save(coupon);

        // 4. Entity → DTO 변환
        return CouponResponseDto.from(savedCoupon);
    }


    @Override
    public CouponVerifyResponseDto verifyCoupon(String couponToken) {
        return null;
    }
}
