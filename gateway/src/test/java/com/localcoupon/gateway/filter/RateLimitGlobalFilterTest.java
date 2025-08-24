package com.localcoupon.gateway.filter;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.InetSocketAddress;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class RateLimitGlobalFilterTest {

  private RateLimitGlobalFilter newFilter() {
    return new RateLimitGlobalFilter(new ObjectMapper());
  }

  @Test
  void deviceId없으면_400() {
    // Given
    var request = MockServerHttpRequest.get("/anything")
        .remoteAddress(new InetSocketAddress("127.0.0.1", 12345))
        .build();
    var exchange = MockServerWebExchange.from(request);

    GatewayFilterChain chain = mock(GatewayFilterChain.class);
    given(chain.filter(any())).willReturn(Mono.empty());

    // When
    Mono<Void> result = newFilter().filter(exchange, chain);

    // Then
    StepVerifier.create(result).verifyComplete();
    assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  void 요청_10회까지_통과_11회째_429() {
    // Given
    var filter = newFilter();
    GatewayFilterChain chain = mock(GatewayFilterChain.class);
    given(chain.filter(any())).willReturn(Mono.empty());

    var base = MockServerHttpRequest.get("/anything")
        .remoteAddress(new InetSocketAddress("127.0.0.1", 12345))
        .header("X-Device-Id", "dev-1");

    // When & Then
    for (int i = 1; i <= 11; i++) {
      var exchange = MockServerWebExchange.from(base.build());

      StepVerifier.create(filter.filter(exchange, chain)).verifyComplete();

      if (i < 11) {
        assertThat(exchange.getResponse().getStatusCode()).isNull();
      } else {
        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS);
      }
    }
  }
}
