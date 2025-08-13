package com.localcoupon.couponservice.coupon.common.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Set;

@Component
public class FeignHeaderRelayInterceptor implements RequestInterceptor {

    private static final Set<String> ALLOWED_HEADERS = Set.of(
            "X-USER-ID",
            "Authorization",
            "X-REQUEST-ID",
            "X-REQUEST-EMAIL",
            "X-USER-ROLE"
    );

    @Override
    public void apply(RequestTemplate template) {
        ServletRequestAttributes attrs =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) return;

        HttpServletRequest request = attrs.getRequest();

        for (String headerName : ALLOWED_HEADERS) {
            String headerValue = request.getHeader(headerName);
            if (headerValue != null) {
                template.header(headerName, headerValue);
            }
        }
    }
}
