package com.localcoupon.couponservice.coupon.batch.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.localcoupon.couponservice.coupon.batch.dto.CouponOpenDto;
import com.localcoupon.couponservice.coupon.repository.CouponRedisRepository;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.RowMapper;

@ExtendWith(MockitoExtension.class)
class CouponOpenBatchTest {

  @Mock
  private CouponRedisRepository couponRedisRepository;

  @Mock
  private org.springframework.jdbc.core.JdbcTemplate jdbcTemplate;

  @InjectMocks
  private CouponOpenBatch couponOpenBatch;

  @Test
  @DisplayName("openCoupons: 조회 결과가 비어있으면 Redis 저장을 하지 않는다")
  void openCoupons_noRows_doNothing() {
    // given
    when(jdbcTemplate.query(
        anyString(),
        ArgumentMatchers.<RowMapper<CouponOpenDto>>any(),
        any(LocalDateTime.class),
        any(LocalDateTime.class)
    )).thenReturn(List.of());

    // when
    couponOpenBatch.openCoupons();

    // then
    verify(jdbcTemplate, times(1))
        .query(anyString(), any(RowMapper.class), any(LocalDateTime.class),
            any(LocalDateTime.class));
  }

  @Test
  @DisplayName("openCoupons: 아직 오픈되지 않은 쿠폰이면 Redis에 재고와 TTL을 저장한다")
  void openCoupons_newCoupon_savedToRedis() {
    // given
    long couponId = 1L;
    long limit = 100L;
    LocalDateTime issueEnd = LocalDateTime.now().plusHours(1);
    List<CouponOpenDto> rows = List.of(CouponOpenDto.of(couponId, limit, issueEnd));

    when(jdbcTemplate.query(
        anyString(),
        ArgumentMatchers.<RowMapper<CouponOpenDto>>any(),
        any(LocalDateTime.class),
        any(LocalDateTime.class)
    )).thenReturn(rows);

    String key = "coupon:open:" + couponId;
    when(couponRedisRepository.exists(key)).thenReturn(false);

    ArgumentCaptor<String> keyCap = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<String> valCap = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<Duration> ttlCap = ArgumentCaptor.forClass(Duration.class);

    // when
    couponOpenBatch.openCoupons();

    // then
    verify(couponRedisRepository, times(1)).exists(key);
    verify(couponRedisRepository, times(1))
        .saveData(keyCap.capture(), valCap.capture(), ttlCap.capture());

    assertThat(keyCap.getValue()).isEqualTo(key);
    assertThat(valCap.getValue()).isEqualTo(String.valueOf(limit));

    // TTL은 0보다 크고, 대략 2일 이내면 충분 (코드상 end+1day 기준)
    Duration ttl = ttlCap.getValue();
    assertThat(ttl).isNotNull();
    assertThat(ttl.getSeconds()).isGreaterThan(0L);
    assertThat(ttl).isLessThanOrEqualTo(Duration.ofDays(2));
  }

  @Test
  @DisplayName("openCoupons: 이미 Redis에 존재하면 저장을 생략한다")
  void openCoupons_alreadyOpened_skipSave() {
    // given
    long couponId = 7L;
    long limit = 50L;
    LocalDateTime issueEnd = LocalDateTime.now().plusMinutes(30);
    List<CouponOpenDto> rows = List.of(CouponOpenDto.of(couponId, limit, issueEnd));

    when(jdbcTemplate.query(
        anyString(),
        ArgumentMatchers.<RowMapper<CouponOpenDto>>any(),
        any(LocalDateTime.class),
        any(LocalDateTime.class)
    )).thenReturn(rows);

    String key = "coupon:open:" + couponId;
    when(couponRedisRepository.exists(key)).thenReturn(true);

    // when
    couponOpenBatch.openCoupons();

    // then
    verify(couponRedisRepository, times(1)).exists(key);
    verify(couponRedisRepository, never()).saveData(anyString(), anyString(), any());
  }
}
