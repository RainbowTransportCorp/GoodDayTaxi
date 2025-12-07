package com.gooddaytaxi.infra.gateway.filter;

import com.gooddaytaxi.infra.gateway.exception.*;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.util.List;

@Slf4j
@Component
public class AuthenticationFilter implements GlobalFilter, Ordered {

    private static final List<String> PUBLIC_API_PREFIXES =
            List.of("/internal/", "/v3/api-docs", "/swagger-ui", "/api/v1/auth/");

    private static final String SYSTEM_UUID = "99999999-9999-9999-9999-999999999999";
    private static final String BEARER = "Bearer ";

    private static final String USER_UUID_HEADER = "x-user-UUID";
    private static final String ROLE_HEADER = "X-User-Role";

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();

        // Swagger·Internal API에 인증 강제하면 시스템 내부 흐름이 막히므로 인증 제외
        //감사 처리를 위해 내부용 UUID (SYSTEM) 추가
        if (isPublicPath(path)) {
            log.debug("[Gateway] Public Path → SYSTEM UUID 주입");

            ServerHttpRequest mutated = exchange.getRequest().mutate()
                .header(USER_UUID_HEADER, SYSTEM_UUID)
                .build();

            return chain.filter(exchange.mutate().request(mutated).build());
        }

        String authHeader = extractAuthorizationHeader(exchange);
        if (authHeader == null || !authHeader.startsWith(BEARER)) {
            log.warn("[Gateway] Authorization 헤더 누락 또는 Bearer 포맷 불일치 → (path={})", path);
            throw new TokenMissingException(TokenErrorCode.TOKEN_MISSING);
        }

        String token = authHeader.substring(BEARER.length());

        Claims claims = parseClaims(token);

        String userUUID = claims.get("userId", String.class);

        if (userUUID == null || userUUID.isBlank()) {
            log.warn("[Gateway] JWT Claims에 userId(UUID) 없음 → (path={})", path);
            throw new EmptyClaimsException(TokenErrorCode.INVALID_CLAIMS);
        }

        /*
          역할(role)도 함께 각 서비스에 헤더로 보냄
            - 인증(Gateway)와 인가(Service)의 관심사를 분리하기 위해
            - 서비스는 DB 조회 없이 헤더 기반으로 권한(RBAC) 판단이 가능해짐
         */
        String role = claims.get("role", String.class);

        log.info("[Gateway] 인증 성공 → userUUID={}, role={}, path={}", userUUID, role, path);

        /*
          Downstream 요청에 사용자 정보(UUID/Role)를 주입한다.
            - 내부 서비스는 오직 "신뢰된 사용자 데이터"만 사용하도록 하기 위함
            - JWT 원문은 더 이상 서비스에 노출할 필요 없음 (보안상 안전)
         */
        ServerHttpRequest.Builder mutatedRequest = exchange.getRequest().mutate()
                .header(USER_UUID_HEADER, userUUID);

        if (role != null && !role.isBlank()) {
            mutatedRequest.header(ROLE_HEADER, role);
        }

        return chain.filter(exchange.mutate().request(mutatedRequest.build()).build());
    }

    private boolean isPublicPath(String path) {
        //정규식 대신 startsWith로 빠른 비교 수행
        return PUBLIC_API_PREFIXES.stream().anyMatch(path::startsWith);
    }

    private String extractAuthorizationHeader(ServerWebExchange exchange) {
        //인증 구조(OAuth → JWT 등)가 변경되면 이 지점만 수정하면 됨
        return exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
    }

    /*
      JWT Claims 파싱 및 검증 담당
        - 인증 실패는 필터 단계에서 전부 처리하여 서비스까지 전달되지 않도록 하기 위해
        - 게이트웨이는 시스템 보안의 최전선이기 때문에 이곳에서 모든 토큰 유효성 책임을 갖는다
     */
    private Claims parseClaims(String token) {

        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));

        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

        } catch (ExpiredJwtException e) {
            log.warn("[Gateway] 만료된 JWT → {}", e.getMessage());
            throw new ExpiredException(TokenErrorCode.EXPIRED);

        } catch (MalformedJwtException e) {
            log.warn("[Gateway] 잘못된 JWT 구조 → {}", e.getMessage());
            throw new MalFormedException(TokenErrorCode.MALFORMED);

        } catch (UnsupportedJwtException e) {
            log.warn("[Gateway] 지원하지 않는 JWT → {}", e.getMessage());
            throw new UnsupportedException(TokenErrorCode.UNSUPPORTED);

        } catch (io.jsonwebtoken.security.SignatureException | SecurityException e) {
            log.warn("[Gateway] JWT 서명 불일치 → {}", e.getMessage());
            throw new InvalidSignatureException(TokenErrorCode.INVALID_SIGNATURE);

        } catch (IllegalArgumentException e) {
            log.warn("[Gateway] JWT Claims 비정상 → {}", e.getMessage());
            throw new EmptyClaimsException(TokenErrorCode.INVALID_CLAIMS);
        }
    }

    @Override
    public int getOrder() {
        /*
          인증은 항상 라우팅보다 먼저 처리되어야 한다.
            - 인증되지 않은 요청이 라우팅까지 내려가면 보안 위협이 증가함
            - 우선순위 -1은 Gateway에서 “가장 먼저 실행되는 필터”를 의미함
         */
        return -1;
    }
}
