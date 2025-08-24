package com.localcoupon.gateway.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.localcoupon.gateway.dto.RateLimitInfo;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


@Component
@RequiredArgsConstructor
public class RateLimitGlobalFilter implements GlobalFilter, Ordered {

  private static final int MAX_REQUESTS = 10;
  private static final long MAX_REQUEST_TIME = 5000L;
  private final ObjectMapper objectMapper;
  private final Map<String, RateLimitInfo> rateLimitMap = new ConcurrentHashMap<>();

  @Override
  public Mono<Void> filter(ServerWebExchange exchange,
      org.springframework.cloud.gateway.filter.GatewayFilterChain chain) {
    ServerHttpRequest request = exchange.getRequest();
    String ip = request.getRemoteAddress() != null ? request.getRemoteAddress().getAddress()
        .getHostAddress() : "unknown";
    String deviceId = request.getHeaders().getFirst("X-Device-Id");

    if (deviceId == null || deviceId.isEmpty()) {
      return writeErrorResponse(exchange, HttpStatus.BAD_REQUEST, "기기 정보가 없습니다.");
    }

    String key = ip + "-" + deviceId;
    long now = System.currentTimeMillis();

    RateLimitInfo info = rateLimitMap.compute(key, (k, existing) -> {
      if (existing == null || now - existing.getLastRequestTime() > MAX_REQUEST_TIME) {
        return RateLimitInfo.init(now);
      }
      return existing.calculate();
    });

    if (info.getCounter().get() > MAX_REQUESTS) {
      return writeErrorResponse(exchange, HttpStatus.TOO_MANY_REQUESTS, "요청이 너무 많습니다.");
    }

    return chain.filter(exchange);
  }

  private Mono<Void> writeErrorResponse(ServerWebExchange exchange, HttpStatus status,
      String message) {
    exchange.getResponse().setStatusCode(status);
    exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

    String bodyJson;
    try {
      bodyJson = objectMapper.writeValueAsString(Map.of("error", message));
    } catch (Exception e) {
      bodyJson = "{\"error\":\"Rate limit error\"}";
    }

    DataBuffer buffer = exchange.getResponse().bufferFactory()
        .wrap(bodyJson.getBytes(StandardCharsets.UTF_8));
    return exchange.getResponse().writeWith(Mono.just(buffer));
  }

  @Override
  public int getOrder() {
    return -2; // 인증 필터보다 먼저 실행
  }
}

