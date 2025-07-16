package com.localcoupon.couponservice;

import com.localcoupon.couponservice.common.infra.RedisProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.localcoupon.couponservice.common.external") //FeignClient 빈 주입
@EnableConfigurationProperties(RedisProperties.class)
public class CouponserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CouponserviceApplication.class, args);
	}

}
