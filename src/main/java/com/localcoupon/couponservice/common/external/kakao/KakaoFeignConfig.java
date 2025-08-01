package com.localcoupon.couponservice.common.external.kakao;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KakaoFeignConfig {
    @Value("${kakao.api.key}")
    private String kakaoApiKey;

    @Bean
    public RequestInterceptor kakaoRequestInterceptor() {
        return requestTemplate -> requestTemplate.header("Authorization", "KakaoAK " + kakaoApiKey);
    };
}
