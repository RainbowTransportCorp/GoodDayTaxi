package com.gooddaytaxi.payment.infrastructure.security;

import com.gooddaytaxi.payment.domain.enums.UserRole;
import java.util.UUID;

public record UserPrincipal(
    UUID userId,
    UserRole role
) {}
