package com.localcoupon.otherservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.localcoupon.otherservice.common.external") //FeignClient 빈 주입
@EnableAsync
public class OtherserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(OtherserviceApplication.class, args);
	}

}
