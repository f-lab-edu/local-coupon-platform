package com.localcoupon.couponservice.common.aspect;

import com.localcoupon.couponservice.common.annotation.AspectComponent;
import com.localcoupon.couponservice.common.annotation.PreventDuplicateRequest;
import com.localcoupon.couponservice.common.enums.CommonErrorCode;
import com.localcoupon.couponservice.common.exception.CommonException;
import com.localcoupon.couponservice.common.infra.RedisProperties;
import com.localcoupon.couponservice.coupon.repository.CouponRedisRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Duration;

@AspectComponent //스프링 AOP는 빈을 감싸서 프록시 객체를 만든다.
//BeanPostProcessor가 원본 빈을 프록시 객체로 교체한다.
@RequiredArgsConstructor
@Slf4j
public class PreventDuplicateAspect {

    private final CouponRedisRepository couponRedisRepository;
    private final RedisProperties redisProperties;

    @Around("@annotation(preventDuplicateRequest)")
    public Object preventDuplicateRequest(ProceedingJoinPoint joinPoint, //메소드 호출 정보를 가로챈 객체
                                          PreventDuplicateRequest preventDuplicateRequest) throws Throwable {
        String key = generateKey();

        if (couponRedisRepository.exists(key)) {
            log.error("[PrventDuplicateAspect] Duplicate request detected: {}", key);
            throw new CommonException(CommonErrorCode.DUPLICATE_ERROR);
        }

        // Redis에 key 기록, TTL 설정
        couponRedisRepository.saveData(key,redisProperties.duplicateRequestLockPrefix(),
                Duration.ofSeconds(preventDuplicateRequest.expireSeconds()));

        Object result = joinPoint.proceed(); //실제 메소드 수행
        couponRedisRepository.deleteData(key); // 레디스 키 삭제
        return result;
    }

    private String generateKey() {
        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                        .getRequest();

        String uri = request.getRequestURI();
        String method = request.getMethod();
        String user = request.getHeader("Authorization");

        return redisProperties.duplicateRequestLockPrefix() + ":" + user + ":" + uri + method;
    }
}
