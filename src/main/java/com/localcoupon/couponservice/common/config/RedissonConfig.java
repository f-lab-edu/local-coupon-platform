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
        config.useSentinelServers()
                .setMasterName("redis-master")  // Redis Sentinel에서 관리하는 마스터 이름
                .addSentinelAddress(
                        "redis://sentinel1:26379",  // Sentinel1 (Docker 컨테이너 명을 사용)
                        "redis://sentinel2:26379",  // Sentinel2
                        "redis://sentinel3:26379"   // Sentinel3
                )
                .setTimeout(3000)
                .setCheckSentinelsList(false)
                .setConnectTimeout(3000);
        config.setCodec(new StringCodec()); // Byte에서 JSONJacksonCodec으로 변경
        return Redisson.create(config);
    }
}
