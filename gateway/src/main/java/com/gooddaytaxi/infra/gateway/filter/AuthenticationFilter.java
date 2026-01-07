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

    /* =========================
     * Path 그룹 정의
     * ========================= */

    // 🔥 인증 완전 패스 (회원가입 / 로그인)
    private static final List<String> AUTH_API_PREFIXES = List.of(
        "/api/v1/auth/"
    );

    // SYSTEM 권한 주입 대상
    private static final List<String> SYSTEM_API_PREFIXES = List.of(
        "/internal/",
        "/v3/api-docs",
        "/swagger-ui"
    );

    /* ========================= */

    private static final String SYSTEM_UUID = "99999999-9999-9999-9999-999999999999";
    private static final String BEARER = "Bearer ";

    private static final String USER_UUID_HEADER = "X-User-UUID";
    private static final String ROLE_HEADER = "X-User-Role";

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();

        /* --------------------------------------------------
         * 1. 회원가입 / 로그인 → 🔥완전 패스
         * -------------------------------------------------- */
        if (isAuthPath(path)) {
            return chain.filter(exchange);
        }

        /* --------------------------------------------------
         * 2. SYSTEM API → SYSTEM 헤더 주입
         * -------------------------------------------------- */
        if (isSystemPath(path)) {
            ServerHttpRequest mutated = exchange.getRequest().mutate()
                .header(USER_UUID_HEADER, SYSTEM_UUID)
                .header(ROLE_HEADER, "SYSTEM")
                .build();

            return chain.filter(exchange.mutate().request(mutated).build());
        }

        /* --------------------------------------------------
         * 3. 일반 보호 API → JWT 인증
         * -------------------------------------------------- */
        String authHeader = exchange.getRequest()
            .getHeaders()
            .getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith(BEARER)) {
            log.warn("[Gateway] Authorization 헤더 누락 또는 Bearer 포맷 불일치 (path={})", path);
            return writeError(exchange, HttpStatus.UNAUTHORIZED, TokenErrorCode.TOKEN_MISSING);
        }

        Claims claims;
        try {
            SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));

            claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(authHeader.substring(BEARER.length()))
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

        String userUUID = claims.get("userId", String.class);
        String role = claims.get("role", String.class);

        if (userUUID == null || userUUID.isBlank()) {
            return writeError(exchange, HttpStatus.BAD_REQUEST, TokenErrorCode.INVALID_CLAIMS);
        }

        log.info("[Gateway] 인증 성공 → userUUID={}, role={}, path={}", userUUID, role, path);

        ServerHttpRequest mutated = exchange.getRequest().mutate()
            .header(USER_UUID_HEADER, userUUID)
            .header(ROLE_HEADER, role)
            .build();

        return chain.filter(exchange.mutate().request(mutated).build());
    }

    /* =========================
     * Utils
     * ========================= */

    private boolean isAuthPath(String path) {
        return AUTH_API_PREFIXES.stream().anyMatch(path::startsWith);
    }

    private boolean isSystemPath(String path) {
        return SYSTEM_API_PREFIXES.stream().anyMatch(path::startsWith);
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

    @Override
    public int getOrder() {
        return -1;
    }
}
