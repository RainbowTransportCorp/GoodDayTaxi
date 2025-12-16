package com.gooddaytaxi.dispatch.infrastructure.logging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Around("execution(* com.gooddaytaxi.dispatch.application..*(..))")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {

        long start = System.currentTimeMillis();

        try {
            log.info("[START] {} args={}",
                    joinPoint.getSignature(),
                    joinPoint.getArgs()
            );

            Object result = joinPoint.proceed();

            long time = System.currentTimeMillis() - start;
            log.info("[END] {} time={}ms",
                    joinPoint.getSignature(),
                    time
            );

            return result;

        } catch (Exception e) {
            log.error("[EXCEPTION] {} message={}",
                    joinPoint.getSignature(),
                    e.getMessage(),
                    e
            );
            throw e;
        }
    }
}

