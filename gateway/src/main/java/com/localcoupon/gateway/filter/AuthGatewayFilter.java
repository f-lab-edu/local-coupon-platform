package com.localcoupon.gateway.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.localcoupon.gateway.dto.UserSessionDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
@Slf4j
@RequiredArgsConstructor
public class AuthGatewayFilter implements GlobalFilter,Ordered {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        log.info("Request path: {}", path);
        // 회원가입, 로그인 경로에 대해서는 인증을 건너뛰도록 처리
        if (path.startsWith("/api/v1/auth/login") || path.startsWith("/api/v1/users/signup")) {
            return chain.filter(exchange);
        }


        String authHeader = exchange.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return onUnauthorized(exchange, "토큰이 없습니다.");
        }

        String redisKey = "SESSION:" + authHeader.substring(7);

        return Mono.fromCallable(() -> redisTemplate.opsForValue().get(redisKey))
                .flatMap(sessionJson -> {
                    if (sessionJson == null) {
                        return onUnauthorized(exchange, "세션이 없습니다.");
                    }
                    try {
                        UserSessionDto session = objectMapper.readValue(sessionJson, UserSessionDto.class);
                        // 유저 ID를 헤더로 downstream 서비스에 전파
                        ServerHttpRequest mutated = exchange.getRequest().mutate()
                                .header("X-USER-ID", String.valueOf(session.id()))
                                .header("X-USER-EMAIL", session.email())
                                .header("X-USER-ROLE", session.roles().get(0))
                                .build();
                        return chain.filter(exchange.mutate().request(mutated).build());
                    } catch (Exception e) {
                        return onUnauthorized(exchange, "세션 파싱 실패");
                    }
                })
                .switchIfEmpty(onUnauthorized(exchange, "인증 실패"));
    }

    private Mono<Void> onUnauthorized(ServerWebExchange exchange, String message) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        String body = "{\"error\":\"" + message + "\"}";
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return -1; // 먼저 실행
    }
}
