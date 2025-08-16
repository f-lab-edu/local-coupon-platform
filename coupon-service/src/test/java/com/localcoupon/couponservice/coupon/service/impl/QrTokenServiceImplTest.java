package com.localcoupon.couponservice.coupon.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.localcoupon.couponservice.coupon.common.exception.CommonErrorCode;
import com.localcoupon.couponservice.coupon.common.exception.CommonException;
import com.localcoupon.couponservice.coupon.service.ImageService;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class QrTokenServiceImplTest {

  @Mock
  private ImageService imageService;

  @InjectMocks
  private QrTokenServiceImpl qrTokenService;

  // helpers
  private static String joinToken(String random, Long id, LocalDateTime start, LocalDateTime end) {
    return String.join("_", random, id.toString(), start.toString(), end.toString());
  }

  private static String base64(String raw) {
    return Base64.getEncoder().encodeToString(raw.getBytes(StandardCharsets.UTF_8));
  }

  @Test
  @DisplayName("generateQrToken: 발급ID/기간이 들어간 Base64 토큰 생성")
  void generateQrToken_shouldContainIssuedIdAndTimes() {
    // given
    Long issuedId = 42L;
    LocalDateTime start = LocalDateTime.now().minusMinutes(1);
    LocalDateTime end = LocalDateTime.now().plusMinutes(10);

    // when
    String token = qrTokenService.generateQrToken(issuedId, start, end);

    // then (랜덤 prefix 는 검증하지 않고, 나머지 3개만 확인)
    String decoded = new String(Base64.getDecoder().decode(token), StandardCharsets.UTF_8);
    String[] parts = decoded.split("_");

    assertThat(parts.length).isEqualTo(4);
    assertThat(parts[1]).isEqualTo(issuedId.toString());
    assertThat(parts[2]).isEqualTo(start.toString());
    assertThat(parts[3]).isEqualTo(end.toString());
  }

  @Test
  @DisplayName("isTokenValid: now가 [start,end] 범위 안이면 true")
  void isTokenValid_true_whenNowInRange() {
    // given
    Long issuedId = 1L;
    LocalDateTime start = LocalDateTime.now().minusMinutes(1);
    LocalDateTime end = LocalDateTime.now().plusMinutes(1);
    String token = base64(joinToken("RANDOM", issuedId, start, end));

    // when
    boolean valid = qrTokenService.isTokenValid(token);

    // then
    assertThat(valid).isTrue();
  }

  @Test
  @DisplayName("isTokenValid: now가 start 이전이면 false")
  void isTokenValid_false_whenBeforeStart() {
    Long issuedId = 1L;
    LocalDateTime start = LocalDateTime.now().plusMinutes(5);
    LocalDateTime end = LocalDateTime.now().plusMinutes(10);
    String token = base64(joinToken("RANDOM", issuedId, start, end));

    boolean valid = qrTokenService.isTokenValid(token);

    assertThat(valid).isFalse();
  }

  @Test
  @DisplayName("isTokenValid: now가 end 이후이면 false")
  void isTokenValid_false_whenAfterEnd() {
    Long issuedId = 1L;
    LocalDateTime start = LocalDateTime.now().minusMinutes(10);
    LocalDateTime end = LocalDateTime.now().minusMinutes(1);
    String token = base64(joinToken("RANDOM", issuedId, start, end));

    boolean valid = qrTokenService.isTokenValid(token);

    assertThat(valid).isFalse();
  }

  @Test
  @DisplayName("isTokenValid: 포맷이 잘못된 경우 false")
  void isTokenValid_false_whenMalformed() {
    String decodedMalformed = "only-two:parts";
    String token = base64(decodedMalformed);

    boolean valid = qrTokenService.isTokenValid(token);

    assertThat(valid).isFalse();
  }

  @Test
  @DisplayName("uploadQrImage: ImageService가 URL 반환 시 그 값을 그대로 반환")
  void uploadQrImage_returnsUrl_whenUploadSucceeds() {
    // given
    String token = "abc";
    String expectedUrl = "https://cdn.example.com/issued-coupon/abc.png";
    given(imageService.uploadQrImage(org.mockito.ArgumentMatchers.any(),
        org.mockito.ArgumentMatchers.eq("issued-coupon/" + token)))
        .willReturn(expectedUrl);

    // when
    String url = qrTokenService.uploadQrImage(token);

    // then
    assertThat(url).isEqualTo(expectedUrl);
    verify(imageService).uploadQrImage(org.mockito.ArgumentMatchers.any(),
        org.mockito.ArgumentMatchers.eq("issued-coupon/" + token));
  }

  @Test
  @DisplayName("uploadQrImage: 내부 예외 발생 시 CommonException(QR_CREATE_OPERATION_ERROR)로 래핑")
  void uploadQrImage_wrapsException() {
    // given
    String token = "err";
    given(imageService.uploadQrImage(org.mockito.ArgumentMatchers.any(),
        org.mockito.ArgumentMatchers.eq("issued-coupon/" + token)))
        .willThrow(new RuntimeException("boom"));

    // when
    CommonException ex = assertThrows(CommonException.class,
        () -> qrTokenService.uploadQrImage(token));

    // then
    assertThat(ex.getErrorCode()).isEqualTo(CommonErrorCode.QR_CREATE_OPERATION_ERROR);
  }
}
