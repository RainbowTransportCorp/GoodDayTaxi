package com.gooddaytaxi.payment.infrastructure.audit;

import com.gooddaytaxi.payment.infrastructure.security.UserPrincipal;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class AuditorAwareImpl implements AuditorAware<UUID> {

    private static final UUID SYSTEM_UUID =
        UUID.fromString("99999999-9999-9999-9999-999999999999");

    @Override
    public Optional<UUID> getCurrentAuditor() {

        Authentication authentication =
            SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.of(SYSTEM_UUID);
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof UserPrincipal userPrincipal) {
            return Optional.of(userPrincipal.userId());
        }

        return Optional.of(SYSTEM_UUID);
    }
}
