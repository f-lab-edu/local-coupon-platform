package com.localcoupon.couponservice.common.external.kakao;

import com.localcoupon.couponservice.common.external.KakaoGeocodeFeignClient;
import com.localcoupon.couponservice.common.external.kakao.dto.KakaoGeocodeInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KakaoGeocodeService {
    private final KakaoGeocodeFeignClient kakaoGeocodeFeignClient;

    public KakaoGeocodeInfoDto geocode(String address) {
        return KakaoGeocodeInfoDto.from(kakaoGeocodeFeignClient.searchAddress(address));
    }
}
