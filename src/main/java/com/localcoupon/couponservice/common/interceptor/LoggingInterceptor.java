package com.localcoupon.couponservice.common.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class LoggingInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("[LoggingInterceptor] 요청 URI : {}, 요청 메소드 : {}, 요청 헤더 : {} ", request.getRequestURI(), request.getMethod(), request.getHeaderNames());
        return true;
    }
}
