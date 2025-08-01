package com.localcoupon.couponservice.common.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.localcoupon.couponservice.auth.enums.AuthErrorCode;
import com.localcoupon.couponservice.common.dto.RateLimitInfo;
import com.localcoupon.couponservice.common.dto.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class RateLimitInterceptor implements HandlerInterceptor {

    private static final int MAX_REQUESTS = 10;
    private static final long MAX_REQUEST_TIME = 5000; // 5초

    private final ObjectMapper objectMapper;

    private final Map<String, RateLimitInfo> rateLimitMap = new ConcurrentHashMap<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        String ip = request.getRemoteAddr();
        String deviceId = request.getHeader("X-Device-Id");

        if (deviceId.isEmpty()) {
            objectMapper.writeValueAsString(ErrorResponse.of(AuthErrorCode.DEVICE_INFO_NOT_EXIST));
            return false;
        }

        if (isRateLimitExceeded(ip + "-" + deviceId, System.currentTimeMillis())) {
            objectMapper.writeValueAsString(ErrorResponse.of(AuthErrorCode.RATE_LIMIT_EXCEEDED));
            return false;
        }

        return true;
    }

    private boolean isRateLimitExceeded(String key, long now) {
        return rateLimitMap.compute(key, (k, info) -> {
            if (info == null || now - info.lastRequestTime() > MAX_REQUEST_TIME) {
                return RateLimitInfo.init(now);
            }
            return info.calculate();
        }).counter().get() > MAX_REQUESTS;
    }
}
