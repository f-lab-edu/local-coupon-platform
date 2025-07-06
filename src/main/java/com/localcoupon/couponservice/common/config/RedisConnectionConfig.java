package com.localcoupon.couponservice.common.config;

import org.redisson.api.RedissonClient;
import org.redisson.spring.data.connection.RedissonConnectionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConnectionConfig {

    @Bean
    public RedisTemplate<String, Integer> redisTemplate(
            @Qualifier("redissonConnectionFactory") RedisConnectionFactory redissonConnectionFactory
    ) {
        RedisTemplate<String, Integer> template = new RedisTemplate<>();
        template.setConnectionFactory(redissonConnectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericToStringSerializer<>(Integer.class));
        return template;
    }


    @Bean(name = "lettuceConnectionFactory")
    @Primary
    public RedisConnectionFactory lettuceConnectionFactory() {
        return new LettuceConnectionFactory();
    }

    @Bean(name = "redissonConnectionFactory")
    public RedisConnectionFactory redissonConnectionFactory(
            @Qualifier("redissonClient") RedissonClient redissonClient) {
        return new RedissonConnectionFactory(redissonClient);
    }
}
