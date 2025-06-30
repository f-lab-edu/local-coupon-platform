package com.localcoupon.couponservice.common.external;

import com.localcoupon.couponservice.common.external.kakao.KakaoFeignConfig;
import com.localcoupon.couponservice.common.external.kakao.dto.KakaoGeocodeResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "kakaoGeocodeClient",
        url = "https://dapi.kakao.com",
        configuration = KakaoFeignConfig.class
)
public interface KakaoGeocodeFeignClient {

    @GetMapping("/v2/local/search/address.json")
    KakaoGeocodeResponse searchAddress(
            @RequestParam("query") String query
    );
}

