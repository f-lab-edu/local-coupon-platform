package com.localcoupon.couponservice.coupon.service.impl;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.localcoupon.common.enums.Result;
import com.localcoupon.couponservice.coupon.entity.Coupon;
import com.localcoupon.couponservice.coupon.entity.CouponPeriod;
import com.localcoupon.couponservice.coupon.entity.IssuedCoupon;
import com.localcoupon.couponservice.coupon.enums.CouponScope;
import com.localcoupon.couponservice.coupon.repository.IssuedCouponRepository;
import com.localcoupon.couponservice.coupon.service.QrTokenService;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * CouponPostProcessServiceImpl 단위 테스트 - QR 토큰 생성 → 업로드 → 메일 발송 → 발급쿠폰 저장 흐름 검증 - 예외 발생 시 로깅만 하고
 * 종료되는지(예외 전파 안 됨) 검증
 */
@ExtendWith(MockitoExtension.class)
class CouponPostProcessServiceImplTest {

  @Mock
  private QrTokenService qrTokenService;
  @Mock
  private CouponMailService couponMailService;
  @Mock
  private IssuedCouponRepository issuedCouponRepository;

  @InjectMocks
  private CouponPostProcessServiceImpl service;

  private Coupon sampleCoupon(LocalDateTime start, LocalDateTime end) {
    return new Coupon(
        CouponScope.LOCAL,
        "테스트 쿠폰",
        "설명",
        10,
        0,
        new CouponPeriod(start, end),
        new CouponPeriod(start, end),
        1L // storeId 버전 도메인 기준
    );
  }

  @Test
  @DisplayName("후처리 성공: 토큰 생성 → 업로드 → 메일 전송 → 저장까지 호출된다")
  void sendQrCouponToUser_success_flow() {
    // given
    String email = "user@example.com";
    Long issuedId = 123L;
    LocalDateTime start = LocalDateTime.now().minusDays(1);
    LocalDateTime end = LocalDateTime.now().plusDays(1);

    Coupon coupon = sampleCoupon(start, end);

    // IssuedCoupon은 도메인 내부 변경(postProcess) 호출을 검증하기 쉽도록 mock 사용
    IssuedCoupon issued = mock(IssuedCoupon.class);
    when(issued.getId()).thenReturn(issuedId);
    when(issued.getCoupon()).thenReturn(coupon);

    // postProcess 호출 후 저장될 오브젝트도 mock으로 준비
    IssuedCoupon updated = mock(IssuedCoupon.class);
    when(issued.postProcess("QR_TOKEN", "https://img.example.com/qr.png")).thenReturn(updated);

    when(qrTokenService.generateQrToken(issuedId, start, end)).thenReturn("QR_TOKEN");
    when(qrTokenService.uploadQrImage("QR_TOKEN")).thenReturn("https://img.example.com/qr.png");
    when(couponMailService.sendCouponEmail(email, coupon.getTitle(),
        "https://img.example.com/qr.png"))
        .thenReturn(Result.SUCCESS);

    // when
    service.sendQrCouponToUser(email, issued);

    // then
    verify(qrTokenService, times(1)).generateQrToken(issuedId, start, end);
    verify(qrTokenService, times(1)).uploadQrImage("QR_TOKEN");
    verify(couponMailService, times(1))
        .sendCouponEmail(email, "테스트 쿠폰", "https://img.example.com/qr.png");
    verify(issuedCouponRepository, times(1)).save(updated);
  }

  @Test
  @DisplayName("업로드 단계에서 예외 발생해도 메서드는 예외를 던지지 않는다(로그만)")
  void sendQrCouponToUser_uploadFails_noThrow() {
    // given
    String email = "user@example.com";
    Long issuedId = 777L;
    LocalDateTime start = LocalDateTime.now().minusHours(1);
    LocalDateTime end = LocalDateTime.now().plusHours(1);
    Coupon coupon = sampleCoupon(start, end);

    IssuedCoupon issued = mock(IssuedCoupon.class);
    when(issued.getId()).thenReturn(issuedId);
    when(issued.getCoupon()).thenReturn(coupon);

    when(qrTokenService.generateQrToken(issuedId, start, end)).thenReturn("QR_TOKEN");
    // 업로드에서 런타임 예외 발생
    when(qrTokenService.uploadQrImage("QR_TOKEN")).thenThrow(new RuntimeException("upload failed"));

    // when & then (예외 전파되지 않아야 함)
    assertDoesNotThrow(() -> service.sendQrCouponToUser(email, issued));

    // 저장 및 메일 발송은 호출되지 않아야 함
    verify(couponMailService, never()).sendCouponEmail(anyString(), anyString(), anyString());
    verify(issuedCouponRepository, never()).save(any(IssuedCoupon.class));
  }

  @Test
  @DisplayName("메일 단계에서 예외 발생해도 메서드는 예외를 던지지 않는다(로그만)")
  void sendQrCouponToUser_mailFails_noThrow() {
    // given
    String email = "user@example.com";
    Long issuedId = 888L;
    LocalDateTime start = LocalDateTime.now().minusMinutes(5);
    LocalDateTime end = LocalDateTime.now().plusMinutes(30);
    Coupon coupon = sampleCoupon(start, end);

    IssuedCoupon issued = mock(IssuedCoupon.class);
    when(issued.getId()).thenReturn(issuedId);
    when(issued.getCoupon()).thenReturn(coupon);

    when(qrTokenService.generateQrToken(issuedId, start, end)).thenReturn("QR_TOKEN");
    when(qrTokenService.uploadQrImage("QR_TOKEN")).thenReturn("https://img.example.com/qr.png");
    // 메일 전송에서 예외
    when(couponMailService.sendCouponEmail(anyString(), anyString(), anyString()))
        .thenThrow(new RuntimeException("mail failed"));

    // when & then
    assertThatCode(() -> service.sendQrCouponToUser(email, issued)).doesNotThrowAnyException();

    // 메일에서 실패했으므로 저장은 되지 않아야 함
    verify(issuedCouponRepository, never()).save(any(IssuedCoupon.class));
  }
}
