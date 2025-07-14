package com.localcoupon.couponservice.common.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.setCodec(new StringCodec()); // Byte에서 JSONJacksonCodec으로 변경
        config.useSingleServer()
                .setAddress("redis://127.0.0.1:6379")
                .setConnectionPoolSize(2) //Netty의 Nonblocking I/O 커넥션 하나로 큐에 담아 처리
                .setConnectionMinimumIdleSize(2)
                .setTimeout(3000);

        return Redisson.create(config);
    }
}
