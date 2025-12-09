package com.gooddaytaxi.infra.gateway.filter;

import com.gooddaytaxi.infra.gateway.exception.TokenErrorCode;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Component
public class AuthenticationFilter implements GlobalFilter, Ordered {

    private static final List<String> PUBLIC_API_PREFIXES =
            List.of("/internal/", "/v3/api-docs", "/swagger-ui", "/api/v1/auth/");

    private static final String SYSTEM_UUID = "99999999-9999-9999-9999-999999999999";
    private static final String BEARER = "Bearer ";

    private static final String USER_UUID_HEADER = "X-User-UUID";
    private static final String ROLE_HEADER = "X-User-Role";

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();

        // ---------------------------
        // 1. 공개 API → SYSTEM UUID 주입
        // ---------------------------
        if (isPublicPath(path)) {
            ServerHttpRequest mutated = exchange.getRequest().mutate()
                .header(USER_UUID_HEADER, SYSTEM_UUID)
                .build();

            return chain.filter(exchange.mutate().request(mutated).build());
        }

        // ---------------------------
        // 2. Authorization 헤더 추출
        // ---------------------------
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith(BEARER)) {
            log.warn("[Gateway] Authorization 헤더 누락 또는 Bearer 포맷 불일치 (path={})", path);
            return writeError(exchange, HttpStatus.UNAUTHORIZED, TokenErrorCode.TOKEN_MISSING);
        }

        // ---------------------------
        // 3. JWT 파싱
        // ---------------------------
        String token = authHeader.substring(BEARER.length());
        Claims claims;

        try {
            SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));

            claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

        } catch (ExpiredJwtException e) {
            log.warn("[Gateway] JWT 만료");
            return writeError(exchange, HttpStatus.UNAUTHORIZED, TokenErrorCode.EXPIRED);

        } catch (MalformedJwtException e) {
            log.warn("[Gateway] 잘못된 JWT 구조");
            return writeError(exchange, HttpStatus.BAD_REQUEST, TokenErrorCode.MALFORMED);

        } catch (UnsupportedJwtException e) {
            log.warn("[Gateway] 지원하지 않는 JWT");
            return writeError(exchange, HttpStatus.BAD_REQUEST, TokenErrorCode.UNSUPPORTED);

        } catch (SecurityException | SignatureException e) {
            log.warn("[Gateway] JWT 서명 오류");
            return writeError(exchange, HttpStatus.UNAUTHORIZED, TokenErrorCode.INVALID_SIGNATURE);

        } catch (Exception e) {
            log.warn("[Gateway] JWT Claims 오류: {}", e.getMessage());
            return writeError(exchange, HttpStatus.BAD_REQUEST, TokenErrorCode.INVALID_CLAIMS);
        }

        // ---------------------------
        // 4. Claims 값 검증
        // ---------------------------
        String userUUID = claims.get("userId", String.class);

        if (userUUID == null || userUUID.isBlank()) {
            return writeError(exchange, HttpStatus.BAD_REQUEST, TokenErrorCode.INVALID_CLAIMS);
        }

        String role = claims.get("role", String.class);

        log.info("[Gateway] 인증 성공 → userUUID={}, role={}, path={}", userUUID, role, path);

        // ---------------------------
        // 5. 헤더로 사용자 정보 주입 후 체인 진행
        // ---------------------------
        ServerHttpRequest.Builder mutatedRequest = exchange.getRequest().mutate()
                .header(USER_UUID_HEADER, userUUID);

        if (role != null && !role.isBlank()) {
            mutatedRequest.header(ROLE_HEADER, role);
        }

        return chain.filter(exchange.mutate().request(mutatedRequest.build()).build());
    }



    // ---------------------------
    // 에러 응답을 JSON 으로 내려주는 함수
    // ---------------------------
    private Mono<Void> writeError(ServerWebExchange exchange, HttpStatus status, TokenErrorCode errorCode) {

        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().add("Content-Type", "application/json");

        String body = String.format(
                "{\"error\":\"%s\",\"message\":\"%s\"}",
                errorCode.name(), errorCode.getMessage()
        );

        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);

        return exchange.getResponse()
                .writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(bytes)));
    }



    private boolean isPublicPath(String path) {
        return PUBLIC_API_PREFIXES.stream().anyMatch(path::startsWith);
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
