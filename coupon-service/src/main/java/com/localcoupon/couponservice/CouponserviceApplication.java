package com.localcoupon.couponservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@EnableFeignClients
public class CouponserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CouponserviceApplication.class, args);
	}

}
