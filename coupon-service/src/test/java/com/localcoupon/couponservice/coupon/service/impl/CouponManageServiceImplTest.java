package com.localcoupon.couponservice.coupon.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.localcoupon.couponservice.coupon.common.dto.response.SuccessResponse;
import com.localcoupon.couponservice.coupon.dto.request.CouponCreateRequestDto;
import com.localcoupon.couponservice.coupon.dto.request.CouponUpdateRequestDto;
import com.localcoupon.couponservice.coupon.dto.response.CouponResponseDto;
import com.localcoupon.couponservice.coupon.dto.response.CouponVerifyResponseDto;
import com.localcoupon.couponservice.coupon.entity.Coupon;
import com.localcoupon.couponservice.coupon.entity.CouponPeriod;
import com.localcoupon.couponservice.coupon.entity.IssuedCoupon;
import com.localcoupon.couponservice.coupon.enums.CouponScope;
import com.localcoupon.couponservice.coupon.enums.UserCouponErrorCode;
import com.localcoupon.couponservice.coupon.exception.UserCouponException;
import com.localcoupon.couponservice.coupon.fixture.StoreResponseFixture;
import com.localcoupon.couponservice.coupon.internal.store.StoreServiceClient;
import com.localcoupon.couponservice.coupon.repository.CouponRepository;
import com.localcoupon.couponservice.coupon.repository.IssuedCouponRepository;
import com.localcoupon.couponservice.coupon.service.QrTokenService;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class CouponManageServiceImplTest {

  private final Long ownerId = 1L;
  @Mock
  private CouponRepository couponRepository;
  @Mock
  private IssuedCouponRepository issuedCouponRepository;
  @Mock
  private QrTokenService qrTokenService;
  @Mock
  private StoreServiceClient storeServiceClient;
  @InjectMocks
  private CouponManageServiceImpl couponManageService;
  private CouponCreateRequestDto createRequest;
  private CouponUpdateRequestDto updateRequest;

  @BeforeEach
  void setUp() {
    createRequest = new CouponCreateRequestDto(
        "쿠폰1", "설명1", CouponScope.LOCAL, 100,
        new CouponPeriod(LocalDateTime.now(), LocalDateTime.now().plusDays(30)),
        new CouponPeriod(LocalDateTime.now(), LocalDateTime.now().plusDays(7))
    );

    updateRequest = new CouponUpdateRequestDto(
        "수정된 쿠폰", "수정된 설명", CouponScope.NATIONAL, 50,
        new CouponPeriod(LocalDateTime.now(), LocalDateTime.now().plusDays(60)),
        new CouponPeriod(LocalDateTime.now(), LocalDateTime.now().plusDays(10))
    );
  }

  @Test
  @DisplayName("쿠폰 생성 성공")
  void testCreateCoupon() {
    // given
    Coupon entity = Coupon.from(createRequest, ownerId);
    // id 부여 (저장 후 반환 가정)
    ReflectionTestUtils.setField(entity, "id", 10L);
    given(couponRepository.save(any(Coupon.class))).willReturn(entity);
    given(storeServiceClient.getMyStore(entity.getStoreId())).willReturn(
        SuccessResponse.of(StoreResponseFixture.sample()));

    // when
    CouponResponseDto response = couponManageService.createCoupon(createRequest, ownerId);

    // then
    assertNotNull(response);
    assertEquals("쿠폰1", response.title());
    verify(couponRepository, times(1)).save(any(Coupon.class));
  }

  @Test
  @DisplayName("쿠폰 상세 조회 성공")
  void testGetCouponDetail() {
    // given
    Coupon entity = Coupon.from(createRequest, ownerId);
    ReflectionTestUtils.setField(entity, "id", 11L);
    when(couponRepository.findById(11L)).thenReturn(Optional.of(entity));

    // when
    CouponResponseDto response = couponManageService.getCouponDetail(11L);

    // then
    assertNotNull(response);
    assertEquals("쿠폰1", response.title());
    verify(couponRepository, times(1)).findById(11L);
  }

  @Test
  @DisplayName("쿠폰 수정 성공")
  void testUpdateCoupon() {
    // given
    Long couponId = 12L;
    Coupon entity = Coupon.from(createRequest, ownerId);
    ReflectionTestUtils.setField(entity, "id", couponId);
    when(couponRepository.findById(couponId)).thenReturn(Optional.of(entity));

    // when
    CouponResponseDto response = couponManageService.updateCoupon(couponId, ownerId, updateRequest);

    // then
    assertNotNull(response);
    assertEquals("수정된 쿠폰", response.title());
    // 구현에 따라 save 호출이 있을 수 있음 — 필요시 아래 활성화
    // verify(couponRepository, times(1)).save(any(Coupon.class));
  }

  @Test
  @DisplayName("쿠폰 인증 성공")
  void testVerifyCoupon_success() {
    // given
    String qrToken = "validQrToken";
    Long userId = 99L;

    when(qrTokenService.isTokenValid(anyString())).thenReturn(true);

    IssuedCoupon issued = IssuedCoupon.builder()
        .qrToken(qrToken)
        .isUsed(false)
        .build();
    // 발급(또는 엔티티) ID가 필요하다면 세팅
    ReflectionTestUtils.setField(issued, "id", 100L);

    when(issuedCouponRepository.findByQrToken(qrToken)).thenReturn(Optional.of(issued));

    // when
    CouponVerifyResponseDto response = couponManageService.verifyCoupon(qrToken, userId);

    // then
    assertNotNull(response);
    assertTrue(response.verified());
    verify(issuedCouponRepository, times(1)).findByQrToken(qrToken);
  }

  @Test
  @DisplayName("쿠폰 인증 실패 - 이미 사용된 쿠폰")
  void testVerifyCoupon_couponAlreadyUsed() {
    // given
    String qrToken = "validQrToken";
    Long userId = 1L;

    when(qrTokenService.isTokenValid(anyString())).thenReturn(true);

    IssuedCoupon used = IssuedCoupon.builder()
        .qrToken(qrToken)
        .isUsed(true) // 이미 사용 처리
        .build();
    when(issuedCouponRepository.findByQrToken(qrToken)).thenReturn(Optional.of(used));

    // when & then
    UserCouponException ex = assertThrows(UserCouponException.class,
        () -> couponManageService.verifyCoupon(qrToken, userId));

    assertEquals(UserCouponErrorCode.ALREADY_COUPON_USED, ex.getErrorCode());
  }

  @Test
  @DisplayName("쿠폰 인증 실패 - 쿠폰 미존재")
  void testVerifyCoupon_couponNotFound() {
    // given
    String qrToken = "invalidQrToken";
    Long userId = 1L;

    when(qrTokenService.isTokenValid(anyString())).thenReturn(true);
    when(issuedCouponRepository.findByQrToken(qrToken)).thenReturn(Optional.empty());

    // when & then
    UserCouponException ex = assertThrows(UserCouponException.class,
        () -> couponManageService.verifyCoupon(qrToken, userId));

    assertEquals(UserCouponErrorCode.COUPON_NOT_FOUND, ex.getErrorCode());
  }
}
