package com.localcoupon.couponservice.coupon.common.config;

import java.util.concurrent.Executor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {

  @Bean(name = "couponIssueAsync")
  public Executor appExecutor() {
    ThreadPoolTaskExecutor ex = new ThreadPoolTaskExecutor();
    ex.setCorePoolSize(8);
    ex.setMaxPoolSize(8);
    ex.setQueueCapacity(1000);
    ex.setThreadNamePrefix("coupon-async-");
    ex.initialize();
    return ex;
  }
}
