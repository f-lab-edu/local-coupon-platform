package com.localcoupon.gateway.filter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.localcoupon.gateway.dto.UserSessionDto;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class AuthGatewayFilterTest {

  StringRedisTemplate redisTemplate;
  ValueOperations<String, String> ops;
  ObjectMapper objectMapper;
  AuthGatewayFilter filter;
  GatewayFilterChain chain;

  @BeforeEach
  void setUp() {
    redisTemplate = mock(StringRedisTemplate.class);
    ops = mock(ValueOperations.class);
    given(redisTemplate.opsForValue()).willReturn(ops);

    objectMapper = new ObjectMapper();
    filter = new AuthGatewayFilter(redisTemplate, objectMapper);

    chain = mock(GatewayFilterChain.class);
    given(chain.filter(any())).willReturn(Mono.empty());
  }

  @Test
  void 토큰없으면_401응답한다() {
    // Given
    var req = MockServerHttpRequest.get("/api/v1/test").build();
    var exchange = MockServerWebExchange.from(req);

    // When
    StepVerifier.create(filter.filter(exchange, chain)).verifyComplete();

    // Then
    assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    verify(chain, never()).filter(any());
  }

  @Test
  void 유효세션이면_헤더전파하고_체인호출한다() throws Exception {
    // Given
    String token = "abc.def";
    var session = new UserSessionDto(1L, "test@test.com", "nickname", List.of("USER"));
    String sessionJson = objectMapper.writeValueAsString(session);

    given(ops.get("SESSION:" + token)).willReturn(sessionJson);

    var req = MockServerHttpRequest.get("/api/v1/secure")
        .header("Authorization", "Bearer " + token)
        .build();
    var exchange = MockServerWebExchange.from(req);

    // When
    StepVerifier.create(filter.filter(exchange, chain)).verifyComplete();

    // Then
    ArgumentCaptor<org.springframework.web.server.ServerWebExchange> captor =
        ArgumentCaptor.forClass(org.springframework.web.server.ServerWebExchange.class);
    verify(chain).filter(captor.capture());

    var mutatedHeaders = captor.getValue().getRequest().getHeaders();
    assertThat(mutatedHeaders.getFirst("X-USER-ID")).isEqualTo("1");
    assertThat(mutatedHeaders.getFirst("X-USER-EMAIL")).isEqualTo("u@test.com");
    assertThat(mutatedHeaders.getFirst("X-USER-ROLE")).isEqualTo("USER");
  }

  @Test
  void 세션없으면_401응답한다() {
    // Given
    String token = "no-session";
    given(ops.get("SESSION:" + token)).willReturn(null);

    var req = MockServerHttpRequest.get("/api/v1/secure")
        .header("Authorization", "Bearer " + token)
        .build();
    var exchange = MockServerWebExchange.from(req);

    // When
    StepVerifier.create(filter.filter(exchange, chain)).verifyComplete();

    // Then
    assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    verify(chain, never()).filter(any());
  }
}
