package com.localcoupon.otherservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EntityScan("com.localcoupon.otherservice")
@EnableFeignClients //FeignClient 빈 주입
@EnableAsync
public class OtherserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(OtherserviceApplication.class, args);
	}

}
