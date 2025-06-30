package com.localcoupon.couponservice.common.external.kakao;

import com.localcoupon.couponservice.common.external.KakaoGeocodeFeignClient;
import com.localcoupon.couponservice.common.external.kakao.dto.KakaoGeocodeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KakaoGeocodeService {
    private final KakaoGeocodeFeignClient kakaoGeocodeFeignClient;

    public KakaoGeocodeResponse geocode(String address) {
        return kakaoGeocodeFeignClient.searchAddress(address);
    }
}
