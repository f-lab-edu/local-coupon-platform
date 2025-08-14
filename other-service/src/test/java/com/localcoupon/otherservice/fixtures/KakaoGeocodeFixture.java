package com.localcoupon.otherservice.fixtures;

import com.localcoupon.otherservice.common.external.kakao.dto.KakaoGeocodeInfoDto;
import java.math.BigDecimal;
import java.util.function.UnaryOperator;

public final class KakaoGeocodeFixture {

  private KakaoGeocodeFixture() {
  }

  /**
   * 기본 지오코드 정보
   */
  public static KakaoGeocodeInfoDto geo() {
    return new KakaoGeocodeInfoDto(
        "110105",
        new BigDecimal("37.4979"),
        new BigDecimal("127.0276")
    );
  }

  /**
   * 일부만 바꾼 지오코드 정보
   */
  public static KakaoGeocodeInfoDto geo(UnaryOperator<KakaoGeocodeInfoDto> overrides) {
    return overrides.apply(geo());
  }
}
