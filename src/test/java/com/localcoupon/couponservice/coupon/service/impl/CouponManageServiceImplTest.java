package com.localcoupon.couponservice.coupon.service.impl;

import com.localcoupon.couponservice.coupon.dto.request.CouponCreateRequestDto;
import com.localcoupon.couponservice.coupon.dto.request.CouponUpdateRequestDto;
import com.localcoupon.couponservice.coupon.dto.response.CouponResponseDto;
import com.localcoupon.couponservice.coupon.dto.response.CouponVerifyResponseDto;
import com.localcoupon.couponservice.coupon.entity.Coupon;
import com.localcoupon.couponservice.coupon.entity.IssuedCoupon;
import com.localcoupon.couponservice.coupon.enums.CouponScope;
import com.localcoupon.couponservice.coupon.enums.UserCouponErrorCode;
import com.localcoupon.couponservice.coupon.exception.UserCouponException;
import com.localcoupon.couponservice.coupon.repository.CouponRepository;
import com.localcoupon.couponservice.coupon.repository.IssuedCouponRepository;
import com.localcoupon.couponservice.coupon.service.QrTokenService;
import com.localcoupon.couponservice.store.entity.Store;
import com.localcoupon.couponservice.store.enums.StoreCategory;
import com.localcoupon.couponservice.store.repository.StoreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class CouponManageServiceImplTest {

    @Mock
    private CouponRepository couponRepository;

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private IssuedCouponRepository issuedCouponRepository;

    @Mock
    private QrTokenService qrTokenService;

    @InjectMocks
    private CouponManageServiceImpl couponManageService;

    private CouponCreateRequestDto createRequest;
    private CouponUpdateRequestDto updateRequest;
    private Store store;
    private IssuedCoupon issuedCoupon;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Create Coupon DTO
        createRequest = new CouponCreateRequestDto("쿠폰1", "설명1", CouponScope.LOCAL, 100,
                LocalDateTime.now(), LocalDateTime.now().plusDays(30),
                LocalDateTime.now(), LocalDateTime.now().plusDays(7));

        // Update Coupon DTO
        updateRequest = new CouponUpdateRequestDto("수정된 쿠폰", "수정된 설명", CouponScope.NATIONAL, 50,
                LocalDateTime.now(), LocalDateTime.now().plusDays(60),
                LocalDateTime.now(), LocalDateTime.now().plusDays(10));

        // Sample IssuedCoupon for testing
        issuedCoupon = IssuedCoupon.builder()
                .qrToken("validQrToken")
                .isUsed(false)
                .build();

        store = Store.builder()
                .id(1L)
                .ownerId(1L)
                .name("스타벅스")
                .address("서울시 송파구 법원로 55")
                .category(StoreCategory.CAFE)
                .phoneNumber("02-1234-5678")
                .description("매장 설명입니다.")
                .imageUrl("http://example.com/image.jpg")
                .latitude(new BigDecimal("37.4979"))
                .longitude(new BigDecimal("127.0276"))
                .build();
    }

    // Create Coupon
    @Test
    void testCreateCoupon() {
        // Given
        when(storeRepository.findByOwnerId(1L)).thenReturn(java.util.Optional.of(store));
        Coupon coupon = Coupon.from(createRequest, store);
        when(couponRepository.save(any(Coupon.class))).thenReturn(coupon);

        // When
        CouponResponseDto response = couponManageService.createCoupon(createRequest, 1L);

        // Then
        assertNotNull(response);
        assertEquals("쿠폰1", response.title());
        verify(couponRepository, times(1)).save(any(Coupon.class)); // Ensure save is called once
    }

    // Get Coupon Detail
    @Test
    void testGetCouponDetail() {
        // Given
        Coupon coupon = Coupon.from(createRequest, store);
        when(couponRepository.findById(1L)).thenReturn(java.util.Optional.of(coupon));

        // When
        CouponResponseDto response = couponManageService.getCouponDetail(1L);

        // Then
        assertNotNull(response);
        assertEquals("쿠폰1", response.title());
        verify(couponRepository, times(1)).findById(1L); // Ensure findById is called once
    }

    @Test
    void testUpdateCoupon() {
        // Given
        Coupon coupon = Coupon.from(createRequest, store);
        when(couponRepository.findById(1L)).thenReturn(java.util.Optional.of(coupon));

        // When
        CouponResponseDto response = couponManageService.updateCoupon(1L, 1L, updateRequest);

        // Then
        assertNotNull(response);
        assertEquals("수정된 쿠폰", response.title());
    }

    @Test
    void testVerifyCoupon_success() {
        // Given
        String qrToken = "validQrToken";
        Long userId = 1L;

        when(qrTokenService.isTokenValid(anyString())).thenReturn(true);
        when(issuedCouponRepository.findByQrToken(qrToken)).thenReturn(java.util.Optional.of(issuedCoupon));

        // When
        CouponVerifyResponseDto response = couponManageService.verifyCoupon(qrToken, userId);

        // Then
        assertNotNull(response);
        assertTrue(response.verified());
        assertEquals(issuedCoupon.getId(), response.couponId()); // Verifying coupon ID
        verify(issuedCouponRepository, times(1)).findByQrToken(qrToken); // Verifying findByQrToken was called once
    }

    @Test
    void testVerifyCoupon_couponAlreadyUsed() {
        // Given
        String qrToken = "validQrToken";
        Long userId = 1L;

        when(qrTokenService.isTokenValid(anyString())).thenReturn(true);
        IssuedCoupon usedIssuedCoupon = issuedCoupon.use();
        when(issuedCouponRepository.findByQrToken(qrToken)).thenReturn(java.util.Optional.of(issuedCoupon));

        // When & Then
        UserCouponException exception = assertThrows(UserCouponException.class, () -> {
            couponManageService.verifyCoupon(qrToken, userId);
        });

        assertEquals(UserCouponErrorCode.ALREADY_COUPON_USED, exception.getErrorCode());
    }

    @Test
    void testVerifyCoupon_couponNotFound() {
        // Given
        String qrToken = "invalidQrToken";
        Long userId = 1L;

        when(qrTokenService.isTokenValid(anyString())).thenReturn(true);
        when(issuedCouponRepository.findByQrToken(qrToken)).thenReturn(java.util.Optional.empty());

        // When & Then
        UserCouponException exception = assertThrows(UserCouponException.class, () -> {
            couponManageService.verifyCoupon(qrToken, userId);
        });

        assertEquals(UserCouponErrorCode.COUPON_NOT_FOUND, exception.getErrorCode());
    }
}
