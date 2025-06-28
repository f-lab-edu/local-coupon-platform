package com.localcoupon.couponservice.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.localcoupon.couponservice.auth.dto.UserSessionDto;
import com.localcoupon.couponservice.common.util.RedisUtils;
import com.localcoupon.couponservice.common.util.StringUtils;
import com.localcoupon.couponservice.user.enums.UserErrorCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class AuthFilter extends OncePerRequestFilter {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String AUTH_HEADER = "Authorization";
    private static final String BEARER = "Bearer ";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        extractSessionId(request.getHeader(AUTH_HEADER))
                .ifPresent(this::validateSession);

        filterChain.doFilter(request, response);
    }

    private Optional<String> extractSessionId(String authHeader) {
        if (StringUtils.isNotEmpty(authHeader) && authHeader.startsWith(BEARER)) {
            String sessionId = authHeader.substring(BEARER.length());
            if (StringUtils.isNotEmpty(sessionId)) {
                return Optional.of(sessionId);
            }
        }
        return Optional.empty();
    }

    private void validateSession(String sessionId) {
        String json = redisTemplate.opsForValue().get(RedisUtils.SESSION_PREFIX + sessionId);
        if (StringUtils.isEmpty(json)) {
            throw new InsufficientAuthenticationException(UserErrorCode.USER_NOT_FOUND.getMessage());
        }

        UserSessionDto sessionDto;
        try {
            sessionDto = objectMapper.readValue(json, UserSessionDto.class);
        } catch (IOException e) {
            log.error("[AuthFilter] Redis parse error", e);
            throw new InsufficientAuthenticationException("[AuthFilter] 세션 정보를 파싱할 수 없습니다.");
        }

        //시큐리티 정보 세팅
        setSecurityContextHolder(sessionDto);
    }

    private void setSecurityContextHolder(UserSessionDto sessionDto) {
        List<SimpleGrantedAuthority> authorities = sessionDto.roles().stream()
                .map(SimpleGrantedAuthority::new)
                .toList();

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        sessionDto.email(), //TODO : CustomUserDetails로 대체?
                        null,
                        authorities
                );

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
