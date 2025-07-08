package com.localcoupon.couponservice.common.aspect;

import com.localcoupon.couponservice.common.annotation.AspectComponent;
import com.localcoupon.couponservice.common.annotation.PreventDuplicateRequest;
import com.localcoupon.couponservice.common.dto.LogContext;
import com.localcoupon.couponservice.common.enums.CommonErrorCode;
import com.localcoupon.couponservice.common.exception.CommonException;
import com.localcoupon.couponservice.common.util.LogUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Duration;

import static com.localcoupon.couponservice.common.infra.RedisConstants.DUPLICATE_REQUEST_LOCK;

@AspectComponent //스프링 AOP는 빈을 감싸서 프록시 객체를 만든다.
//BeanPostProcessor가 원본 빈을 프록시 객체로 교체한다.
@RequiredArgsConstructor
public class PreventDuplicateAspect {

    private final RedisTemplate<String, String> redisTemplate;

    @Around("@annotation(preventDuplicateRequest)")
    public Object preventDuplicateRequest(ProceedingJoinPoint joinPoint, //메소드 호출 정보를 가로챈 객체
                                          PreventDuplicateRequest preventDuplicateRequest) throws Throwable {
        String key = generateKey();

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        Boolean exists = redisTemplate.hasKey(key);
        if (Boolean.TRUE.equals(exists)) {
            LogUtils.error(LogContext.of(request, "Duplicate Request"));
            throw new CommonException(CommonErrorCode.DUPLICATE_ERROR);
        }

        // Redis에 key 기록, TTL 설정
        redisTemplate.opsForValue().set(
                key,
                DUPLICATE_REQUEST_LOCK,
                Duration.ofSeconds(preventDuplicateRequest.expireSeconds())
        );


        Object result = joinPoint.proceed(); //실제 메소드 수행
        redisTemplate.delete(key); // 레디스 키 삭제
        return result;
    }

    private String generateKey() {
        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                        .getRequest();

        String uri = request.getRequestURI();
        String method = request.getMethod();
        String user = request.getHeader("Authorization");

        return "DUPLICATE_REQUEST:" + user + ":" + uri + method;
    }
}
