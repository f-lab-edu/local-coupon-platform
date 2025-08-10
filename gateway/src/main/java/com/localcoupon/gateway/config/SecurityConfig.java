// Spring Cloud Gateway에서 사용하는 WebFlux용 보안 설정
package com.localcoupon.gateway.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final ObjectMapper objectMapper;

    private static final String[] NO_AUTH_APIS = {
            "/api/v1/auth/login",
            "/api/v1/users/signup"
    };

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .authorizeExchange(exchange -> exchange
                        .pathMatchers(NO_AUTH_APIS).permitAll()
                        .anyExchange().permitAll()
                )
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .exceptionHandling(exceptionHandlingSpec ->
                        exceptionHandlingSpec
                                .authenticationEntryPoint((exchange, ex) -> {
                                    var res = exchange.getResponse();
                                    res.setStatusCode(HttpStatus.UNAUTHORIZED);
                                    res.getHeaders().setContentType(MediaType.APPLICATION_JSON);
                                    String body = null;
                                    try {
                                        body = objectMapper.writeValueAsString("{\"error\":\"UNAUTHORIZED\"}");
                                    } catch (JsonProcessingException e) {
                                        throw new RuntimeException(e);
                                    }
                                    var buffer = res.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
                                    return res.writeWith(Mono.just(buffer));
                                })
                                .accessDeniedHandler((exchange, denied) -> {
                                    var res = exchange.getResponse();
                                    res.setStatusCode(HttpStatus.FORBIDDEN);
                                    res.getHeaders().setContentType(MediaType.APPLICATION_JSON);
                                    String body = null;
                                    try {
                                        body = objectMapper.writeValueAsString("{\"error\":\"FORBIDDEN\"}");
                                    } catch (JsonProcessingException e) {
                                        throw new RuntimeException(e);
                                    }
                                    var buffer = res.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
                                    return res.writeWith(Mono.just(buffer));
                                })
                );

        return http.build();
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}

