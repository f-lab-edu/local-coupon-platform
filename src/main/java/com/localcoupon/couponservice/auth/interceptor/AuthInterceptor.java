package com.localcoupon.couponservice.auth.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.localcoupon.couponservice.auth.context.AuthContextHolder;
import com.localcoupon.couponservice.auth.enums.AuthErrorCode;
import com.localcoupon.couponservice.global.exception.ErrorCode;
import com.localcoupon.couponservice.global.response.ErrorResponse;
import com.localcoupon.couponservice.global.util.StringUtils;
import com.localcoupon.couponservice.user.enums.UserErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Set;

@RequiredArgsConstructor
@Component
@Slf4j
public class AuthInterceptor implements HandlerInterceptor {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String REDIS_SESSION_PREFIX = "SESSION:";
    private static final Set<String> EXCLUDE_PATHS = Set.of(
            "/api/v1/auth/login",
            "/api/v1/user" // 회원가입 POST
    );
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String uri = request.getRequestURI();

        // 인증이 필요 없는 경로는 바로 통과
        if (isExcluded(uri)) {
            return true;
        }

        String sessionId = request.getHeader("Authorization");
        if (StringUtils.isEmpty(sessionId)) {
            writeErrorResponse(response, AuthErrorCode.SESSION_NOT_EXISTS);
            return false;
        }

        String userEmail = redisTemplate.opsForValue().get(REDIS_SESSION_PREFIX + sessionId);

        if (StringUtils.isEmpty(userEmail)) {
            writeErrorResponse(response, UserErrorCode.USER_NOT_FOUND);
            return false;
        }

        // 유효한 세션 → ThreadLocal 저장
        AuthContextHolder.setUserKey(userEmail);
        return true;
    }

    private boolean isExcluded(String uri) {
        return EXCLUDE_PATHS.contains(uri);
    }


    private boolean validateUserFail(HttpServletResponse response, String userEmail) {
        if (StringUtils.isEmpty(userEmail)) {
            writeErrorResponse(response, UserErrorCode.USER_NOT_FOUND);
            return true;
        }
        return false;
    }

    private boolean validateSessionFail(HttpServletResponse response, String sessionId) {
        if (StringUtils.isEmpty(sessionId)) {
            return true;
        }
        return false;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        // 요청 종료 시 반드시 초기화
        AuthContextHolder.clear();
    }

    private void writeErrorResponse(HttpServletResponse response, ErrorCode errorCode) {
        try {
            ErrorResponse errorResponse = ErrorResponse.of(errorCode);
            //응답 세팅 및 JSON String Write
            response.setStatus(errorCode.getStatus().value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        } catch (IOException e) {
            log.warn(e.getMessage());
        }
    }
}

