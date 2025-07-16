package com.localcoupon.couponservice.coupon.service.impl;

import com.localcoupon.couponservice.common.dto.request.CursorPageRequest;
import com.localcoupon.couponservice.common.dto.response.ResultCodeResponseDto;
import com.localcoupon.couponservice.common.enums.Result;
import com.localcoupon.couponservice.coupon.dto.request.CouponCreateRequestDto;
import com.localcoupon.couponservice.coupon.dto.request.CouponUpdateRequestDto;
import com.localcoupon.couponservice.coupon.dto.response.CouponResponseDto;
import com.localcoupon.couponservice.coupon.dto.response.CouponVerifyResponseDto;
import com.localcoupon.couponservice.coupon.entity.Coupon;
import com.localcoupon.couponservice.coupon.entity.IssuedCoupon;
import com.localcoupon.couponservice.coupon.repository.CouponRepository;
import com.localcoupon.couponservice.coupon.repository.IssuedCouponRepository;
import com.localcoupon.couponservice.coupon.service.CouponManageService;
import com.localcoupon.couponservice.store.entity.Store;
import com.localcoupon.couponservice.store.enums.StoreErrorCode;
import com.localcoupon.couponservice.store.exception.StoreException;
import com.localcoupon.couponservice.store.repository.StoreRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponManageServiceImpl implements CouponManageService {
    private final CouponRepository couponRepository;
    private final StoreRepository storeRepository;
    private final IssuedCouponRepository issuedCouponRepository;

    @Override
    @Transactional
    public CouponResponseDto createCoupon(CouponCreateRequestDto request, Long userId) {
        Store store = storeRepository.findByOwnerId(userId)
                .orElseThrow(() -> new StoreException(StoreErrorCode.STORE_NOT_FOUND_EXCEPTION));

        Coupon savedCoupon = couponRepository.save(Coupon.from(request, store));

        return CouponResponseDto.from(savedCoupon);
    }


    @Override
    @Transactional(readOnly = true)
    public List<CouponResponseDto> getCouponsByOwner(Long ownerId, CursorPageRequest request) {
        List<Coupon> coupons = couponRepository.findAllByOwnerIdWithCursorPaging(ownerId, request);
        return coupons.stream()
                .map(CouponResponseDto::from)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CouponResponseDto getCouponDetail(Long couponId) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(EntityNotFoundException::new);

        return CouponResponseDto.from(coupon);
    }

    @Override
    @Transactional
    public CouponResponseDto updateCoupon(Long couponId, Long userId, CouponUpdateRequestDto request) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(EntityNotFoundException::new);

        coupon.update(request);

        return CouponResponseDto.from(coupon);
    }

    @Override
    @Transactional
    public ResultCodeResponseDto deleteCoupon(Long couponId, Long userId) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(EntityNotFoundException::new);
        coupon.delete();
        return ResultCodeResponseDto.from(Result.SUCCESS);
    }

    @Override
    @Transactional
    public CouponVerifyResponseDto verifyCoupon(String qrToken) {
        // 유저의 발급 내역 조회
        IssuedCoupon issuedCoupon = issuedCouponRepository.findByQrToken(qrToken)
                .orElseThrow(EntityNotFoundException::new);

        //쿠폰 사용처리
        IssuedCoupon usedCoupon = issuedCoupon.use();

        return CouponVerifyResponseDto.of(
                usedCoupon.getCoupon().getId(),
                true
        );
    }
}
