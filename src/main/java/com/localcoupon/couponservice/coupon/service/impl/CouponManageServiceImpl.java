package com.localcoupon.couponservice.coupon.service.impl;

import com.localcoupon.couponservice.common.dto.request.CursorPageRequest;
import com.localcoupon.couponservice.common.enums.Result;
import com.localcoupon.couponservice.coupon.dto.request.CouponCreateRequestDto;
import com.localcoupon.couponservice.coupon.dto.request.CouponUpdateRequestDto;
import com.localcoupon.couponservice.coupon.dto.response.CouponResponseDto;
import com.localcoupon.couponservice.coupon.dto.response.CouponVerifyResponseDto;
import com.localcoupon.couponservice.coupon.dto.response.ListCouponResponseDto;
import com.localcoupon.couponservice.coupon.entity.Coupon;
import com.localcoupon.couponservice.coupon.entity.IssuedCoupon;
import com.localcoupon.couponservice.coupon.enums.UserCouponErrorCode;
import com.localcoupon.couponservice.coupon.exception.UserCouponException;
import com.localcoupon.couponservice.coupon.repository.CouponRepository;
import com.localcoupon.couponservice.coupon.repository.IssuedCouponRepository;
import com.localcoupon.couponservice.coupon.service.CouponManageService;
import com.localcoupon.couponservice.coupon.service.QrTokenService;
import com.localcoupon.couponservice.store.entity.Store;
import com.localcoupon.couponservice.store.enums.StoreErrorCode;
import com.localcoupon.couponservice.store.exception.StoreException;
import com.localcoupon.couponservice.store.repository.StoreRepository;
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
    private final QrTokenService qrTokenService;

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
    public ListCouponResponseDto getCouponsByOwner(Long ownerId, CursorPageRequest request) {
        List<Coupon> coupons = couponRepository.findAllByOwnerIdWithCursorPaging(ownerId, request);
        return ListCouponResponseDto.from(coupons);
    }

    @Override
    @Transactional(readOnly = true)
    public CouponResponseDto getCouponDetail(Long couponId) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new UserCouponException(UserCouponErrorCode.COUPON_NOT_FOUND));

        return CouponResponseDto.from(coupon);
    }

    @Override
    @Transactional
    public CouponResponseDto updateCoupon(Long couponId, Long userId, CouponUpdateRequestDto request) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new UserCouponException(UserCouponErrorCode.COUPON_NOT_FOUND));

        coupon.update(request);

        return CouponResponseDto.from(coupon);
    }

    @Override
    @Transactional
    public Result deleteCoupon(Long couponId, Long userId) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new UserCouponException(UserCouponErrorCode.COUPON_NOT_FOUND));

        return coupon.delete();
    }

    @Override
    @Transactional
    public CouponVerifyResponseDto verifyCoupon(String qrToken, Long userId) {
        // QR 토큰에서 유효기간과 쿠폰 ID 추출
        qrTokenService.isTokenValid(qrToken);

        // 유저의 발급 내역 조회
        IssuedCoupon issuedCoupon = issuedCouponRepository.findByQrToken(qrToken)
                .orElseThrow(() -> new UserCouponException(UserCouponErrorCode.COUPON_NOT_FOUND));

        // 이미 사용된 쿠폰은 처리하지 않음
        if (issuedCoupon.isUsed()) {
            throw new UserCouponException(UserCouponErrorCode.ALREADY_COUPON_USED);
        }

        // 쿠폰 사용 처리
        IssuedCoupon usedCoupon = issuedCoupon.use();

        // 사용처리된 쿠폰 정보를 DTO로 반환
        return CouponVerifyResponseDto.of(
                usedCoupon.getId(),
                true
        );
    }
}
