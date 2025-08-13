//package com.localcoupon.otherservice.coupon.batch.service;
//
//import com.localcoupon.couponservice.coupon.batch.service.CouponStockSyncBatch;
//import com.localcoupon.otherservice.coupon.CouponData;
//import com.localcoupon.couponservice.coupon.entity.Coupon;
//import com.localcoupon.couponservice.coupon.enums.UserCouponErrorCode;
//import com.localcoupon.couponservice.coupon.exception.UserCouponException;
//import com.localcoupon.couponservice.coupon.repository.CouponRedisRepository;
//import com.localcoupon.couponservice.coupon.repository.CouponRepository;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Spy;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.time.Clock;
//import java.util.List;
//import java.util.Optional;
//
//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//public class CouponStockSyncBatchTest {
//    @Mock
//    private CouponRedisRepository couponRedisRepository;
//    @Mock
//    private CouponRepository couponRepository;
//    @InjectMocks
//    @Spy
//    private CouponStockSyncBatch batchService; // syncSingleCoupon이 있는 클래스
//
//    Clock fixedClock;
//
//
//    @Test
//    @DisplayName("스케줄러가 Redis 키를 가져와 각각 동기화한다.")
//    void syncIssuedCount_정상동작() {
//        // GIVEN
//        List<String> redisKeys = List.of("coupon:open:1", "coupon:open:2");
//        when(couponRedisRepository.getAllOpenCouponKeys()).thenReturn(redisKeys);
//        // WHEN
//        batchService.syncIssuedCount();
//        // THEN
//        verify(batchService, times(2)).syncSingleCoupon(any(String.class));
//    }
//
//    @Test
//    @DisplayName("쿠폰 재고를 DB에 동기화한다 - 정상 케이스")
//    void syncSingleCoupon_정상동작() {
//        // GIVEN
//        String redisKey = "coupon:open:1";
//        Long couponId = 1L;
//        String redisValue = "10";
//        Coupon coupon = CouponData.activeCoupon(couponId, Clock.systemDefaultZone());
//
//        when(couponRedisRepository.getValue(eq(redisKey), eq(String.class)))
//                .thenReturn(Optional.of(redisValue));
//        when(couponRepository.findById(couponId))
//                .thenReturn(Optional.of(coupon));
//        when(couponRepository.save(any(Coupon.class)))
//                .thenAnswer(invocation -> invocation.getArgument(0)); // 저장된 쿠폰 반환
//
//        // WHEN
//        int result = batchService.syncSingleCoupon(redisKey);
//
//        // THEN
//        assertThat(result).isEqualTo(Integer.parseInt(redisValue));
//        verify(couponRepository).findById(couponId);
//        verify(couponRepository).save(any(Coupon.class));
//    }
//
//    @Test
//    @DisplayName("쿠폰ID를 레디스에서 추출한다.")
//    void 쿠폰ID가정상등록된경우_레디스키에서_추출한다() {
//        //given
//        Coupon coupon = CouponData.activeCoupon(1L, Clock.systemDefaultZone());
//        String key = "coupon:open:" + coupon.getId();
//        //when
//        Long couponId = Optional.of(List.of(key.split(":")))
//                .filter(list -> list.size() == 3)
//                .map(list -> Long.parseLong(list.get(2)))
//                .orElseThrow(() -> new UserCouponException(UserCouponErrorCode.COUPON_KEY_PARSING_FAILED));
//        //then
//        Assertions.assertEquals(couponId, 1L);
//    }
//    @Test
//    @DisplayName("쿠폰ID가 잘못등록된 경우 예외를 반환한다.")
//    void 쿠폰ID가잘못등록된경우_예외반환() {
//        //given
//        Coupon coupon = CouponData.activeCoupon(1L, Clock.systemDefaultZone());
//        String key = "coupon:open:" + coupon.getId();
//        //when then
//        Assertions.assertThrows(UserCouponException.class, () -> Optional.of(List.of(key.split(":")))
//                .filter(list -> list.size() == 3)
//                .filter(list -> list.get(1).equals("open"))
//                .map(list -> Long.parseLong(list.get(2)))
//                .orElseThrow(() -> new UserCouponException(UserCouponErrorCode.COUPON_KEY_PARSING_FAILED)));
//    }
//}
