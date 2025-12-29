package com.gooddaytaxi.payment.infrastructure.security;

import com.gooddaytaxi.payment.domain.enums.UserRole;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class GatewayAuthFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {

        String userId = request.getHeader("X-User-UUID");
        String role = request.getHeader("X-User-Role");

        if (userId != null && role != null &&
            SecurityContextHolder.getContext().getAuthentication() == null) {

            UserPrincipal principal =
                new UserPrincipal(
                    UUID.fromString(userId),
                    UserRole.valueOf(role)
                );

            Authentication auth =
                new UsernamePasswordAuthenticationToken(
                    principal,
                    null,
                    List.of(new SimpleGrantedAuthority("ROLE_" + role))
                );

            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(request, response);
    }
}

