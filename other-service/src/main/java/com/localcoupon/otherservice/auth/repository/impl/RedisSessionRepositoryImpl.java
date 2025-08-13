package com.localcoupon.otherservice.auth.repository.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.localcoupon.otherservice.auth.dto.UserSessionDto;
import com.localcoupon.otherservice.auth.repository.SessionRepository;
import com.localcoupon.otherservice.common.exception.CommonErrorCode;
import com.localcoupon.otherservice.common.exception.CommonException;
import com.localcoupon.otherservice.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class RedisSessionRepositoryImpl implements SessionRepository {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public boolean save(String sessionToken, UserSessionDto sessionDto) {
        try {
            redisTemplate.opsForValue().set(
                    "SESSION:" + sessionToken,
                    objectMapper.writeValueAsString(sessionDto),
                    Duration.ofSeconds(3600)
            );
        } catch (JsonProcessingException e) {
            throw new CommonException(CommonErrorCode.JSON_SERIALIZE_ERROR);
        }
        return true;
    }

    @Override
    public boolean delete(String sessionToken) {
        redisTemplate.delete("SESSION:" + sessionToken);
        return true;
    }

    @Override
    public UserSessionDto get(String sessionToken) {
        String json = redisTemplate.opsForValue().get("SESSION:" + sessionToken);
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
