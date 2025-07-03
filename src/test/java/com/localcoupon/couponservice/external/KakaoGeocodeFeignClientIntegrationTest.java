package com.localcoupon.couponservice.external;

import com.localcoupon.couponservice.common.external.KakaoGeocodeFeignClient;
import com.localcoupon.couponservice.common.external.kakao.dto.KakaoGeocodeResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class KakaoGeocodeFeignClientIntegrationTest {

    @Autowired
    KakaoGeocodeFeignClient kakaoGeocodeFeignClient;

    @Test
    void testSearchAddress() {
        KakaoGeocodeResponse response =
                kakaoGeocodeFeignClient.searchAddress("서울특별시 송파구 법원로 55");

        System.out.println(response);
    }
}
