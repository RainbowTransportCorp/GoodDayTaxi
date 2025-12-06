package com.gooddaytaxi.dispatch.infrastructure.audit;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;
import java.util.UUID;

/**
 * 전 서비스에서 동일하게 가져가야 하는 감사 entity 필터 도구
 * 이거 없으면 createdBy, updateBy 제대로 안 들어갑니다.
 * jpaConfig도 있어야 하고 이것도 있어야 합니다.
 */
@Component
public class AuditorAwareImpl implements AuditorAware<UUID> {

    private static final UUID SYSTEM_UUID =
        UUID.fromString("99999999-9999-9999-9999-999999999999");

    private static final String USER_UUID_HEADER = "x-user-UUID";

    @Override
    public Optional<UUID> getCurrentAuditor() {

        // HTTP 요청이 아닌 내부 호출이면 RequestContext 없음 → SYSTEM 처리
        RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
        if (!(attrs instanceof ServletRequestAttributes servletAttrs)) {
            return Optional.of(SYSTEM_UUID);
        }

        // HTTP 요청 헤더에서 UUID 추출
        String uuid = servletAttrs.getRequest().getHeader(USER_UUID_HEADER);

        // Gateway가 SYSTEM UUID 를 넣어줌 → 그대로 반환
        if (uuid != null && !uuid.isBlank()) {
            try {
                return Optional.of(UUID.fromString(uuid));
            } catch (IllegalArgumentException ignore) {
                // 잘못된 UUID 구조 → SYSTEM 처리
            }
        }

        // fallback: 내부 호출 or 헤더 누락 → SYSTEM UUID
        return Optional.of(SYSTEM_UUID);
    }
}
