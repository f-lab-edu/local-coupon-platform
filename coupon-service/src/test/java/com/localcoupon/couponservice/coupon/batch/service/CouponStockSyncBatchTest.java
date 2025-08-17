package com.localcoupon.couponservice.coupon.batch.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.localcoupon.couponservice.coupon.enums.CouponStock;
import com.localcoupon.couponservice.coupon.enums.UserCouponErrorCode;
import com.localcoupon.couponservice.coupon.exception.UserCouponException;
import com.localcoupon.couponservice.coupon.fixture.CouponFixture;
import com.localcoupon.couponservice.coupon.repository.CouponRedisRepository;
import com.localcoupon.couponservice.coupon.repository.CouponRepository;
import java.time.Clock;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CouponStockSyncBatchTest {

  @Mock
  private CouponRedisRepository couponRedisRepository;
  @Mock
  private CouponRepository couponRepository;

  // syncSingleCoupon 호출 수만 검증할 때 실제 메서드 실행 막기 위해 Spy 사용 + doReturn
  @Spy
  @InjectMocks
  private CouponStockSyncBatch batchService;

  @Test
  @DisplayName("스케줄러가 Redis 키를 가져와 각각 동기화한다.")
  void syncIssuedCount_callsSyncSingleForEachKey() {
    // GIVEN
    List<String> redisKeys = List.of("coupon:open:1", "coupon:open:2");
    when(couponRedisRepository.getAllOpenCouponKeys()).thenReturn(redisKeys);
    // Spy가 실제 메서드 실행하지 않도록 막음
    doReturn(0).when(batchService).syncSingleCoupon(anyString());

    // WHEN
    batchService.syncIssuedCount();

    // THEN
    verify(batchService, times(2)).syncSingleCoupon(anyString());
  }

  @Test
  @DisplayName("쿠폰 재고(또는 발급수) 값을 Redis에서 읽어와 DB에 동기화 - 정상 케이스")
  void syncSingleCoupon_ok() {
    // GIVEN
    String redisKey = "coupon:open:1";
    Long couponId = 1L;
    String redisValue = "10";

    when(couponRedisRepository.getValue(redisKey, String.class))
        .thenReturn(Optional.of(redisValue));
    when(couponRepository.findById(couponId))
        .thenReturn(Optional.of(CouponFixture.activeCoupon(couponId, Clock.systemDefaultZone())));
    when(couponRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

    // WHEN
    int result = batchService.syncSingleCoupon(redisKey);

    // THEN
    assertThat(result).isEqualTo(Integer.parseInt(redisValue));
    verify(couponRepository).findById(couponId);
    verify(couponRepository).save(any());
  }

  @Test
  @DisplayName("Redis 값이 없으면 SOLD_OUT 반환하며 DB 저장하지 않는다")
  void syncSingleCoupon_noRedisValue_returnsSoldOut() {
    // GIVEN
    String redisKey = "coupon:open:99";
    when(couponRedisRepository.getValue(redisKey, String.class))
        .thenReturn(Optional.empty());

    // WHEN
    int result = batchService.syncSingleCoupon(redisKey);

    // THEN
    assertThat(result).isEqualTo(CouponStock.SOLD_OUT.getValue());
    verify(couponRepository, never()).save(any());
  }

  @Test
  @DisplayName("DB에 쿠폰이 없으면 SOLD_OUT 반환")
  void syncSingleCoupon_couponNotFound_returnsSoldOut() {
    // GIVEN
    String redisKey = "coupon:open:123";
    when(couponRedisRepository.getValue(redisKey, String.class))
        .thenReturn(Optional.of("5"));
    when(couponRepository.findById(123L)).thenReturn(Optional.empty());

    // WHEN
    int result = batchService.syncSingleCoupon(redisKey);

    // THEN
    assertThat(result).isEqualTo(CouponStock.SOLD_OUT.getValue());
  }

  @Test
  @DisplayName("잘못된 키 형식이면 파싱 예외(UserCouponException) 발생")
  void syncSingleCoupon_badKey_throws() {
    assertThatThrownBy(() -> batchService.syncSingleCoupon("coupon:bad"))
        .isInstanceOf(UserCouponException.class)
        .extracting("errorCode")
        .isEqualTo(UserCouponErrorCode.COUPON_KEY_PARSING_FAILED);
  }
}
