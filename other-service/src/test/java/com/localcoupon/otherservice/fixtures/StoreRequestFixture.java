package com.localcoupon.otherservice.fixtures;

import com.localcoupon.otherservice.store.dto.request.StoreRequestDto;
import com.localcoupon.otherservice.store.enums.StoreCategory;
import java.util.function.UnaryOperator;

public final class StoreRequestFixture {

  private StoreRequestFixture() {
  }

  /**
   * 기본 요청 DTO
   */
  public static StoreRequestDto request() {
    return new StoreRequestDto(
        "스타벅스",
        "서울시 송파구 법원로 55",
        StoreCategory.CAFE,
        "02-1234-5678",
        "매장 설명입니다.",
        "http://example.com/image.jpg"
    );
  }

  /**
   * 일부 필드만 바꾼 요청 DTO
   */
  public static StoreRequestDto request(UnaryOperator<StoreRequestDto> overrides) {
    return overrides.apply(request());
  }
}
