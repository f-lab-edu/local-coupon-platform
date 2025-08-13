package com.localcoupon.couponservice.coupon.service.impl;

import com.localcoupon.common.enums.Result;
import com.localcoupon.couponservice.coupon.common.contract.store.StoreResponseDto;
import com.localcoupon.couponservice.coupon.common.dto.response.SuccessResponse;
import com.localcoupon.couponservice.coupon.dto.CursorPageRequest;
import com.localcoupon.couponservice.coupon.dto.request.CouponCreateRequestDto;
import com.localcoupon.couponservice.coupon.dto.request.CouponUpdateRequestDto;
import com.localcoupon.couponservice.coupon.dto.response.CouponResponseDto;
import com.localcoupon.couponservice.coupon.dto.response.CouponVerifyResponseDto;
import com.localcoupon.couponservice.coupon.dto.response.ListCouponResponseDto;
import com.localcoupon.couponservice.coupon.entity.Coupon;
import com.localcoupon.couponservice.coupon.entity.IssuedCoupon;
import com.localcoupon.couponservice.coupon.enums.UserCouponErrorCode;
import com.localcoupon.couponservice.coupon.exception.UserCouponException;
import com.localcoupon.couponservice.coupon.internal.store.StoreServiceClient;
import com.localcoupon.couponservice.coupon.internal.store.dto.StoreSummaryDto;
import com.localcoupon.couponservice.coupon.repository.CouponRepository;
import com.localcoupon.couponservice.coupon.repository.IssuedCouponRepository;
import com.localcoupon.couponservice.coupon.service.CouponManageService;
import com.localcoupon.couponservice.coupon.service.QrTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CouponManageServiceImpl implements CouponManageService {
    private final CouponRepository couponRepository;
    private final IssuedCouponRepository issuedCouponRepository;
    private final QrTokenService qrTokenService;
    private final StoreServiceClient storeServiceClient;

    @Override
    @Transactional
    public CouponResponseDto createCoupon(CouponCreateRequestDto request, Long userId) {
        StoreResponseDto store = Optional.ofNullable(storeServiceClient.getMyStore(userId))
                .map(SuccessResponse::getData)
                .orElseThrow(() -> new UserCouponException(UserCouponErrorCode.STORE_NOT_FOUND_EXCEPTION));

        Coupon savedCoupon = couponRepository.save(Coupon.from(request, store.id()));

        return CouponResponseDto.from(savedCoupon);
    }

    @Override
    @Transactional(readOnly = true)
    public ListCouponResponseDto getCouponsByOwner(Long ownerId, CursorPageRequest request) {
        StoreResponseDto store = Optional.ofNullable(storeServiceClient.getMyStore(ownerId))
                .map(SuccessResponse::getData)
                .orElseThrow(() -> new UserCouponException(UserCouponErrorCode.STORE_NOT_FOUND_EXCEPTION));

        return ListCouponResponseDto.from(couponRepository.findAllByOwnerIdWithCursorPaging(ownerId, request),
                StoreSummaryDto.of(store));
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
