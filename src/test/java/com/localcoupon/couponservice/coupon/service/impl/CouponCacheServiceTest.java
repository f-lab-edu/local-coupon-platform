//package com.localcoupon.couponservice.coupon.service.impl;
//
//import com.localcoupon.couponservice.common.external.kakao.dto.KakaoGeocodeInfoDto;
//import com.localcoupon.couponservice.coupon.entity.Coupon;
//import com.localcoupon.couponservice.coupon.enums.CouponScope;
//import com.localcoupon.couponservice.coupon.exception.UserCouponException;
//import com.localcoupon.couponservice.coupon.service.CouponCacheService;
//import com.localcoupon.couponservice.store.dto.request.StoreRequestDto;
//import com.localcoupon.couponservice.store.entity.Store;
//import com.localcoupon.couponservice.store.enums.StoreCategory;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.core.ValueOperations;
//import org.springframework.data.redis.core.script.DefaultRedisScript;
//import org.springframework.test.util.ReflectionTestUtils;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.concurrent.TimeUnit;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.Mockito.*;
//
//class CouponCacheServiceTest {
//
//    @Mock
//    private RedisTemplate<String, Integer> redisTemplate;
//
//    @Mock
//    private ValueOperations<String, Integer> valueOperations;
//
//    @InjectMocks
//    private CouponCacheService couponCacheService;
//
//
//    private final Long couponId = 1L;
//    private final String redisKey = COUPON_OPEN_PREFIX + couponId;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    @DisplayName("쿠폰을 레디스에 저장한다_Stock과 TTL 설정")
//    void saveCouponForOpen_shouldSaveCouponStockAndSetTTL() {
//        // given
//        Coupon coupon = createCoupon();
//        ReflectionTestUtils.setField(coupon, "id", 1L);
//
//        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
//
//        // when
//        couponCacheService.saveCouponForOpen(coupon);
//
//        // then
//        verify(valueOperations).set(redisKey, coupon.getTotalCount());
//        verify(redisTemplate).expire(eq(redisKey), anyLong(), eq(TimeUnit.SECONDS));
//    }
//
//    @Test
//    @DisplayName("쿠폰이 오픈상태인지 레디스에 있는지 체크한다) ")
//    void isCouponOpen_shouldReturnTrue_whenKeyExists() {
//        // given
//        when(redisTemplate.hasKey(redisKey)).thenReturn(true);
//
//        // when
//        boolean result = couponCacheService.isCouponOpen(couponId);
//
//        // then
//        assertThat(result).isTrue();
//    }
//
//    @Test
//    @DisplayName("쿠폰이 레디스에 존재하지 않는다.")
//    void isCouponOpen_shouldReturnFalse_whenKeyDoesNotExist() {
//        // given
//        when(redisTemplate.hasKey(redisKey)).thenReturn(false);
//
//        // when
//        boolean result = couponCacheService.isCouponOpen(couponId);
//
//        // then
//        assertThat(result).isFalse();
//    }
//
//    @Test
//    @DisplayName("쿠폰 재고감소 로직 실행 시 매진이다.")
//    void decreaseCouponStock_shouldThrowException_whenStockIsSoldOut() {
//        // given
//        when(redisTemplate.execute(any(DefaultRedisScript.class), eq(List.of(redisKey))))
//                .thenReturn(0);
//
//        // when & then
//        assertThrows(UserCouponException.class,
//                () -> couponCacheService.decreaseCouponStock(couponId));
//
//    }
//
//    @Test
//    @DisplayName("쿠폰 재고감소 로직 실행 정상")
//    void decreaseCouponStock_shouldReturnTrue_whenStockIsAvailable() {
//        // given
//        when(redisTemplate.execute(any(DefaultRedisScript.class), eq(List.of(redisKey))))
//                .thenReturn(1);
//
//        // when
//        int result = couponCacheService.decreaseCouponStock(couponId);
//
//        // then
//        assertThat(result).isEqualTo(1);
//    }
//
//
//    private Coupon createCoupon() {
//        StoreRequestDto request = new StoreRequestDto(
//                "스타벅스",
//                "서울시 송파구 법원로 55",
//                StoreCategory.CAFE,
//                "02-1234-5678",
//                "매장 설명입니다.",
//                "http://example.com/image.jpg"
//        );
//
//        KakaoGeocodeInfoDto geoCodeInfo = new KakaoGeocodeInfoDto(
//                "110105",
//                new BigDecimal("37.4979"),
//                new BigDecimal("127.0276")
//        );
//
//        return new Coupon(
//                CouponScope.LOCAL,
//                "Test Coupon",
//                "Coupon description",
//                10,
//                0,
//                LocalDateTime.now(),
//                LocalDateTime.now().plusDays(7),
//                LocalDateTime.now(),
//                LocalDateTime.now().plusDays(7),
//                Store.from(request,geoCodeInfo, 1L)
//        );
//    }
//}
