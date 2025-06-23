package com.localcoupon.couponservice.global.config;

import com.localcoupon.couponservice.auth.interceptor.AuthInterceptor;
import static com.localcoupon.couponservice.global.constants.ApiMapping.API_Prefix;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@RequiredArgsConstructor
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    private final AuthInterceptor authInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns(API_Prefix.API_V1 + "/**");
    }
}
