package com.localcoupon.couponservice.common.external.kakao.dto;

import com.localcoupon.couponservice.common.external.kakao.enums.KakaoErrorCode;
import com.localcoupon.couponservice.common.external.kakao.exception.KakaoGeoCodeException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

public record KakaoGeocodeInfoDto(
        String regionCode,
        BigDecimal latitude,
        BigDecimal longitude
) {
    public static KakaoGeocodeInfoDto from(
            KakaoGeocodeResponse response
    ) {
        //Document 확인
        KakaoGeocodeResponse.Document geoCodeResponse = Optional.ofNullable(response.documents())
                .filter(list -> !list.isEmpty())
                .map(list -> list.get(0))
                .orElseThrow(() -> new KakaoGeoCodeException(KakaoErrorCode.DATA_SEARCH_FAILED));

        //행정동 코드 확인
        String regionCode = Optional.ofNullable(geoCodeResponse.address())
                .map(KakaoGeocodeResponse.Address::h_code)
                .orElse(null);

        //Y 위도 확인
        BigDecimal latitude = Optional.ofNullable(geoCodeResponse.y())
                .map(BigDecimal::new)
                .map(lat -> lat.setScale(6, RoundingMode.HALF_UP))
                .orElse(null);

        //X 경도 확인
        BigDecimal longitude = Optional.ofNullable(geoCodeResponse.x())
                .map(BigDecimal::new)
                .map(lon -> lon.setScale(6, RoundingMode.HALF_UP))
                .orElse(null);

        return new KakaoGeocodeInfoDto(regionCode, latitude, longitude);
    }
    public static KakaoGeocodeInfoDto of(
            String regionCode,
            BigDecimal latitude,
            BigDecimal longitude
    ) {
       return new KakaoGeocodeInfoDto(regionCode, latitude, longitude);
    }
}