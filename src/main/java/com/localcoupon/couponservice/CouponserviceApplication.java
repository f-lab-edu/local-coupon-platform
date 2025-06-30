package com.localcoupon.couponservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.localcoupon.couponservice.common.external") //FeignClient 빈 주입
public class CouponserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CouponserviceApplication.class, args);
	}

}
