package com.localcoupon.otherservice.coupon.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.localcoupon.couponservice.CouponserviceApplication;
import com.localcoupon.couponservice.coupon.dto.CursorPageRequest;
import com.localcoupon.couponservice.coupon.dto.request.CouponCreateRequestDto;
import com.localcoupon.couponservice.coupon.entity.Coupon;
import com.localcoupon.couponservice.coupon.entity.CouponPeriod;
import com.localcoupon.couponservice.coupon.enums.CouponScope;
import com.localcoupon.couponservice.coupon.repository.CouponRepository;
import com.localcoupon.couponservice.coupon.repository.IssuedCouponRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.hibernate.query.SortDirection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(classes = CouponserviceApplication.class)
@ActiveProfiles("local")
class CouponRepositoryTest {

  @Autowired
  private CouponRepository couponRepository;

  @Autowired
  private IssuedCouponRepository issuedCouponRepository;


  @BeforeEach
  void setUp() {
    issuedCouponRepository.deleteAllInBatch();
    issuedCouponRepository.flush();
    couponRepository.deleteAllInBatch();
    couponRepository.flush();

    // 쿠폰 생성/저장
    CouponCreateRequestDto createRequest1 = new CouponCreateRequestDto(
        "쿠폰1", "설명1", CouponScope.LOCAL, 100,
        new CouponPeriod(LocalDateTime.now(), LocalDateTime.now().plusDays(30)),
        new CouponPeriod(LocalDateTime.now(), LocalDateTime.now().plusDays(7))
    );

    CouponCreateRequestDto createRequest2 = new CouponCreateRequestDto(
        "쿠폰2", "설명2", CouponScope.LOCAL, 200,
        new CouponPeriod(LocalDateTime.now(), LocalDateTime.now().plusDays(30)),
        new CouponPeriod(LocalDateTime.now(), LocalDateTime.now().plusDays(7))
    );

    Coupon coupon1 = Coupon.from(createRequest1, 1L);
    Coupon coupon2 = Coupon.from(createRequest2, 1L);

    couponRepository.save(coupon1);
    couponRepository.save(coupon2);
    couponRepository.flush();
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
    assertEquals(2, coupons.size());
    assertEquals("쿠폰1", coupons.get(0).getTitle());
    assertEquals("쿠폰2", coupons.get(1).getTitle());
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
    assertEquals(2, coupons.size());
    assertEquals("쿠폰2", coupons.get(0).getTitle());
    assertEquals("쿠폰1", coupons.get(1).getTitle());
  }
}
