package com.gooddaytaxi.dispatch.infrastructure.aop;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class MdcAspect {

    private static final String ROLE_HEADER = "X-User-Role";
    private static final String USER_HEADER = "X-User-UUID";

    @Around("@within(org.springframework.web.bind.annotation.RestController)")
    public Object applyMdc(ProceedingJoinPoint pjp) throws Throwable {
        try {
            HttpServletRequest request = currentRequest();
            if (request != null) {
                putIfPresent("actor", request.getHeader(ROLE_HEADER));
                putIfPresent("userId", request.getHeader(USER_HEADER));
            }
            return pjp.proceed();
        } finally {
            MDC.clear();
        }
    }

    private HttpServletRequest currentRequest() {
        ServletRequestAttributes attrs =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attrs != null ? attrs.getRequest() : null;
    }

    private void putIfPresent(String key, String value) {
        if (value != null && !value.isBlank()) {
            MDC.put(key, value);
        }
    }
}
