package com.localcoupon.couponservice.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.localcoupon.couponservice.auth.dto.UserSessionDto;
import com.localcoupon.couponservice.auth.security.CustomUserDetails;
import com.localcoupon.couponservice.common.config.SecurityConfig;
import com.localcoupon.couponservice.common.infra.RedisProperties;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class AuthFilter extends OncePerRequestFilter {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final RedisProperties redisProperties;

    private static final String AUTH_HEADER = "Authorization";
    private static final String BEARER = "Bearer ";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        if (Arrays.asList(SecurityConfig.NO_AUTH_APIS).contains(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        extractSessionId(request.getHeader(AUTH_HEADER))
                .map(this::validateSession)
                .ifPresent(auth ->
                        SecurityContextHolder.getContext().setAuthentication(auth)
                );


        filterChain.doFilter(request, response);
    }

    private Optional<String> extractSessionId(String authHeader) {
        return Optional.ofNullable(authHeader)
                .filter(header -> header.startsWith(BEARER))
                .map(header -> header.substring(BEARER.length()))
                .filter(StringUtils::hasText);
    }


    private UsernamePasswordAuthenticationToken validateSession(String sessionId) {
        String json = redisTemplate.opsForValue().get(redisProperties.sessionPrefix() + sessionId);
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
        return createAuthenticationToken(sessionDto);
    }


    private UsernamePasswordAuthenticationToken createAuthenticationToken(UserSessionDto sessionDto) {
        // CustomUserDetails 생성
        CustomUserDetails customUserDetails = CustomUserDetails.from(sessionDto);
        //인증 완료 토큰 생성
        return UsernamePasswordAuthenticationToken.authenticated(
                        customUserDetails,
                        null,
                        customUserDetails.getAuthorities()
                );
    }
}
