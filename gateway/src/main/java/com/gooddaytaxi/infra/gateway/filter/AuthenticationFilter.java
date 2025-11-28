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

    //내부 통신할 때는 internal로, swagger에서는 /v3/api-docs로 예외처리하여 필터를 동작하지 않게합니다.
    private static final List<String> PUBLIC_API_PREFIXES = List.of("/internal/", "/v3/api-docs", "/swagger-ui");
    private static final String BEARER = "Bearer ";
    private static final String USER_HEADER = "x-user-id";

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();

        boolean isPublic = PUBLIC_API_PREFIXES.stream()
                .anyMatch(path::startsWith);

        //내부 통신일 경우에는 필터를 적용하지 않아야 합니다. (이중 인증 방지)
        if (isPublic) {
            return chain.filter(exchange);
        }

        /*
        Gateway는 전체 외부 요청에 관문역할을 합니다.
        이 시점에서 인증 여부를 단일하게 판단해야 각 서비스에서는 로그인이 되었는지 신경 쓰지 않아도 됩니다.
        인증 실패를 즉시 차단함으로써 downstream 서비스의 보안 위험을 줄입니다.
        */
        String authHeader = extractAuthHeader(exchange);

        /*
          JWT는 반드시 Authorization: Bearer <token> 형태여야 합니다.
          형식이 맞지 않다는 것은 토큰이 아예 없거나 변조되었다는 뜻이므로 즉시 오류가 발생합니다.
         */
        if (authHeader == null || !authHeader.startsWith(BEARER)) {
            throw new TokenMissingException(TokenErrorCode.TOKEN_MISSING);
        }

        String token = authHeader.substring(BEARER.length());

        /*
          토큰 검증은 반드시 Gateway가 중앙에서 수행해야 합니다.
          각 서비스가 개별적으로 검증하면 중복 코드가 생기고 보안 정책도 서비스별로 달라져 위험하기 때문
         */
        Claims claims = parseClaims(token);
        String userId = claims.get("user_id", String.class);

        /*
          Downstream 서비스는 JWT 원문을 알 필요 없음.
          Gateway가 인증을 통과시킨 사용자 ID만 전달해주면,
          각 서비스는 인증이 완료된 사용자 기준으로 처리할 수 있습니다.
         */
        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                .header(USER_HEADER, userId)
                .build();

        return chain.filter(exchange.mutate().request(mutatedRequest).build());
    }

    private String extractAuthHeader(ServerWebExchange exchange) {
        /*
          헤더 추출 로직을 메서드로 분리하면 인증 로직 가독성이 높아지고,
          향후 OAuth2 / 다른 인증 방식과 결합되더라도 확장하기 쉬운 구조가 됩니다.
         */
        return exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
    }

    private Claims parseClaims(String token) {

        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));

        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

        } catch (ExpiredJwtException e) {
            throw new ExpiredException(TokenErrorCode.EXPIRED);

        } catch (MalformedJwtException e) {
            throw new MalFormedException(TokenErrorCode.MALFORMED);

        } catch (UnsupportedJwtException e) {
            throw new UnsupportedException(TokenErrorCode.UNSUPPORTED);

        } catch (io.jsonwebtoken.security.SignatureException | SecurityException e) {
            throw new InvalidSignatureException(TokenErrorCode.INVALID_SIGNATURE);

        } catch (IllegalArgumentException e) {
            throw new EmptyClaimsException(TokenErrorCode.INVALID_CLAIMS);
        }
    }

    @Override
    public int getOrder() {
        /*
          Gateway 필터는 순서가 중요합니다.
          인증은 가장 앞에서 처리되어야 하므로 order = -1로 설정합니다.
          (숫자가 낮을수록 우선순위 높기 때문에)
         */
        return -1;
    }
}
