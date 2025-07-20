package com.localcoupon.couponservice.coupon.repository;

import com.localcoupon.couponservice.common.dto.request.CursorPageRequest;
import com.localcoupon.couponservice.coupon.dto.request.CouponCreateRequestDto;
import com.localcoupon.couponservice.coupon.entity.Coupon;
import com.localcoupon.couponservice.coupon.enums.CouponScope;
import com.localcoupon.couponservice.store.entity.Store;
import com.localcoupon.couponservice.store.enums.StoreCategory;
import com.localcoupon.couponservice.store.repository.StoreRepository;
import org.hibernate.query.SortDirection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class CouponRepositoryTest {

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private StoreRepository storeRepository;

    private Store store;

    @BeforeEach
    void setUp() {
        // Store 데이터 준비
        // 기존 데이터 삭제
        couponRepository.deleteAll(); // 이전에 저장된 쿠폰 데이터를 모두 삭제
        storeRepository.deleteAll();  // 기존 매장 데이터 삭제

        store = Store.builder()
                .ownerId(1L)  // Owner ID 설정
                .name("스타벅스")
                .address("서울시 송파구 법원로 55")
                .category(StoreCategory.CAFE)
                .phoneNumber("02-1234-5678")
                .description("매장 설명입니다.")
                .imageUrl("http://example.com/image.jpg")
                .latitude(new BigDecimal("37.4979"))
                .longitude(new BigDecimal("127.0276"))
                .build();  // Store DB에 저장
        storeRepository.save(store);  // Store DB에 저장

        // Coupon 데이터 준비
        CouponCreateRequestDto createRequest = new CouponCreateRequestDto("쿠폰1", "설명1", CouponScope.LOCAL, 100,
                LocalDateTime.now(), LocalDateTime.now().plusDays(30),
                LocalDateTime.now(), LocalDateTime.now().plusDays(7));

        CouponCreateRequestDto createRequest2 = new CouponCreateRequestDto("쿠폰2", "설명2", CouponScope.LOCAL, 200,
                LocalDateTime.now(), LocalDateTime.now().plusDays(25),
                LocalDateTime.now(), LocalDateTime.now().plusDays(30));

        // Coupon 객체를 DTO에서 변환하여 생성
        Coupon coupon1 = Coupon.from(createRequest, store);  // of() 메서드를 사용하여 Coupon 객체 생성
        Coupon coupon2 = Coupon.from(createRequest2, store);  // of() 메서드를 사용하여 Coupon 객체 생성

        couponRepository.save(coupon1);  // Coupon DB에 저장
        couponRepository.save(coupon2);  // Coupon DB에 저장
    }

    @Test
    @Transactional
    void testFindAllByOwnerIdWithCursorPaging_ASC() {
        // Given
        CursorPageRequest request = new CursorPageRequest(0L, 10, "id", SortDirection.ASCENDING);

        // When
        List<Coupon> coupons = couponRepository.findAllByOwnerIdWithCursorPaging(1L, request);

        // Then
        assertNotNull(coupons);
        assertEquals(2, coupons.size()); // 두 개의 쿠폰이 반환되어야 합니다.
        assertEquals("쿠폰1", coupons.get(0).getTitle());  // 첫 번째 쿠폰 제목이 "쿠폰1"이어야 함
        assertEquals("쿠폰2", coupons.get(1).getTitle());  // 두 번째 쿠폰 제목이 "쿠폰2"이어야 함
    }

    @Test
    @Transactional
    void testFindAllByOwnerIdWithCursorPaging_DESC() {
        // Given
        CursorPageRequest request = new CursorPageRequest(0L, 10, "id", SortDirection.DESCENDING);

        // When
        List<Coupon> coupons = couponRepository.findAllByOwnerIdWithCursorPaging(1L, request);

        // Then
        assertNotNull(coupons);
        assertEquals(2, coupons.size()); // 두 개의 쿠폰이 반환되어야 합니다.
        assertEquals("쿠폰2", coupons.get(0).getTitle());  // 첫 번째 쿠폰 제목이 "쿠폰2"이어야 함
        assertEquals("쿠폰1", coupons.get(1).getTitle());  // 두 번째 쿠폰 제목이 "쿠폰1"이어야 함
    }
}
