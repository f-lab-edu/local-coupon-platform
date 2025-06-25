package com.localcoupon.couponservice.auth.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.localcoupon.couponservice.auth.context.AuthContextHolder;
import com.localcoupon.couponservice.auth.enums.AuthErrorCode;
import com.localcoupon.couponservice.global.dto.LogContext;
import com.localcoupon.couponservice.global.dto.response.ErrorResponse;
import com.localcoupon.couponservice.global.exception.ErrorCode;
import com.localcoupon.couponservice.global.util.ClientUtils;
import com.localcoupon.couponservice.global.util.LogUtils;
import com.localcoupon.couponservice.user.enums.UserErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;

import static com.localcoupon.couponservice.global.util.StringUtils.isEmpty;

@RequiredArgsConstructor
@Component
@Slf4j
public class AuthInterceptor implements HandlerInterceptor {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String REDIS_SESSION_PREFIX = "SESSION:";
    private static final Set<String> EXCLUDE_PATHS = Set.of(
            "/api/v1/auth/login", // 로그인
            "/api/v1/user" // 회원가입 POST
    );
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler)  {
        String uri = request.getRequestURI();

        if (isExcluded(uri)) return true;

        String sessionId = request.getHeader("Authorization");

        return validateSession(sessionId)
                .map(errorCode -> {
                    response.setStatus(errorCode.getStatus().value());
                    //쓰기 작업 수행
                    try {
                        String body = objectMapper.writeValueAsString(ErrorResponse.of(errorCode));
                        response.getWriter().write(body);
                    } catch (IOException e) {
                        LogUtils.error(LogContext.of(
                                request.getRequestURI(),
                                ClientUtils.extractClientIp(request.getHeader("X-Forwarded-For"), request.getRemoteAddr()),
                                request.getMethod(),
                                null,
                                e
                        ));
                    }
                    return false;
                })
                .orElse(true);
    }

    private boolean isExcluded(String uri) {
        return EXCLUDE_PATHS.contains(uri);
    }

    private Optional<ErrorCode> validateSession (String sessionId) {
        if (isEmpty(sessionId)) {
            return Optional.of(AuthErrorCode.SESSION_NOT_EXISTS);
        }

        String userEmail = redisTemplate.opsForValue().get(REDIS_SESSION_PREFIX + sessionId);
        if (isEmpty(userEmail)) {
            return Optional.of(UserErrorCode.USER_NOT_FOUND);
        }
        //AuthContextHolder에 세션 세팅
        AuthContextHolder.setUserKey(userEmail);
        return Optional.empty();
    }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        // 요청 종료 시 반드시 초기화
        AuthContextHolder.clear();
    }
}

