package com.gooddaytaxi.dispatch.infrastructure.audit;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;
import java.util.UUID;

@Component
public class AuditorAwareImpl implements AuditorAware<UUID> {

    @Override
    public Optional<UUID> getCurrentAuditor() {

        var attributes = RequestContextHolder.getRequestAttributes();

        if (attributes instanceof ServletRequestAttributes servletRequestAttributes) {
            String uuid = servletRequestAttributes
                    .getRequest()
                    .getHeader("x-user-UUID");

            if (uuid != null && !uuid.isBlank()) {
                return Optional.of(UUID.fromString(uuid));
            }
        }

        return Optional.empty();
    }
}
