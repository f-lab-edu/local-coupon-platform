package com.localcoupon.couponservice.auth.repository.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.localcoupon.couponservice.auth.dto.UserSessionDto;
import com.localcoupon.couponservice.auth.repository.SessionRepoistory;
import com.localcoupon.couponservice.common.enums.CommonErrorCode;
import com.localcoupon.couponservice.common.exception.CommonException;
import com.localcoupon.couponservice.common.infra.RedisProperties;
import com.localcoupon.couponservice.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisSessionRepositoryImpl implements SessionRepoistory {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final RedisProperties redisProperties;

    @Override
    public boolean save(String sessionToken, UserSessionDto sessionDto) {
        try {
            redisTemplate.opsForValue().set(
                    redisProperties.sessionPrefix() + sessionToken,
                    objectMapper.writeValueAsString(sessionDto),
                    redisProperties.sessionTtl()
            );
        } catch (JsonProcessingException e) {
            throw new CommonException(CommonErrorCode.JSON_SERIALIZE_ERROR);
        }
        return true;
    }

    @Override
    public boolean delete(String sessionToken) {
        redisTemplate.delete(redisProperties.sessionPrefix() + sessionToken);
        return true;
    }

    @Override
    public UserSessionDto get(String sessionToken) {
        String json = redisTemplate.opsForValue().get(redisProperties.sessionPrefix() + sessionToken);
        if (StringUtils.isEmpty(json)) {
            throw new CommonException(CommonErrorCode.ENTITY_NOT_FOUND_ERROR);
        }
        try {
            return objectMapper.readValue(json, UserSessionDto.class);
        } catch (JsonProcessingException e) {
            throw new CommonException(CommonErrorCode.JSON_SERIALIZE_ERROR);
        }
    }
}
