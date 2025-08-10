package com.localcoupon.couponservice.coupon.common.external.kakao;

import com.localcoupon.otherservice.common.external.KakaoGeocodeFeignClient;
import com.localcoupon.otherservice.common.external.kakao.dto.KakaoGeocodeInfoDto;
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
