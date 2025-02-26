package com.example.currency.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    private final ObjectMapper objectMapper;

    public LoggingAspect(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    // 記錄 Controller 層的 API 調用
    @Around("execution(* com.example.currency.controller.*.*(..))")
    public Object logControllerCall(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest();

        // 記錄請求信息
        log.info("API Call - Method: {} URI: {}", request.getMethod(), request.getRequestURI());

        // 記錄請求參數
        Object[] args = joinPoint.getArgs();
        if (args != null && args.length > 0) {
            log.info("Request Body: {}", objectMapper.writeValueAsString(args));
        }

        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long endTime = System.currentTimeMillis();

        // 記錄響應和執行時間
        log.info("Response Body: {}", objectMapper.writeValueAsString(result));
        log.info("Execution Time: {}ms", (endTime - startTime));

        return result;
    }

    // 記錄外部 API 調用
    @Around("execution(* com.example.currency.service.impl.DefaultCoinDeskApiClient.*(..))")
    public Object logExternalApiCall(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("External API Call - Method: {}", joinPoint.getSignature().getName());

        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long endTime = System.currentTimeMillis();

        log.info("External API Response: {}", objectMapper.writeValueAsString(result));
        log.info("External API Call Time: {}ms", (endTime - startTime));

        return result;
    }
}