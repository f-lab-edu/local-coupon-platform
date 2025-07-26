package com.localcoupon.couponservice.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(20);      // 기본 스레드 개수
        executor.setMaxPoolSize(20);       // 최대 스레드 개수
        executor.setQueueCapacity(1000);   // 대기 큐 크기
        executor.setThreadNamePrefix("async-coupon-");
        executor.initialize();
        return executor;
    }
}
