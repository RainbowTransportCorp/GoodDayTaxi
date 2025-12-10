package com.gooddaytaxi.infra.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

/**
 * 각 컨텍스트 간의 통신 시 Param 한글 깨짐을 방지하기 위해
 * 게이트웨이에서 인코딩을 구현합니다.
 */
@Slf4j
@Component
public class DecodeQueryParamFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        URI uri = exchange.getRequest().getURI();
        String rawQuery = uri.getRawQuery();

        // QueryParam이 존재하지 않으면 그대로 진행
        if (rawQuery == null || !rawQuery.contains("%")) {
            return chain.filter(exchange);
        }

        // 인코딩된 QueryParam 디코딩
        String decodedQuery = URLDecoder.decode(rawQuery, StandardCharsets.UTF_8);

        URI newUri = URI.create(
                uri.getScheme() + "://" +
                        uri.getAuthority() +
                        uri.getPath() +
                        (decodedQuery.isBlank() ? "" : "?" + decodedQuery)
        );

        ServerHttpRequest newRequest = exchange.getRequest().mutate().uri(newUri).build();

        log.debug("[Gateway] QueryParam decoded → {}", decodedQuery);

        return chain.filter(exchange.mutate().request(newRequest).build());
    }

    // 인증 필터보다 먼저 실행되도록 순서 -2
    @Override
    public int getOrder() {
        return -2;
    }
}
