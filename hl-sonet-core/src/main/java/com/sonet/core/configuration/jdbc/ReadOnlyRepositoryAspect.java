package com.sonet.core.configuration.jdbc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Order(0)
@RequiredArgsConstructor
@Slf4j
public class ReadOnlyRepositoryAspect {

    @Pointcut("@within(ReadOnlyRepository)")
    public void withinReadOnlyRepository() {}

    @Around("withinReadOnlyRepository()")
    private Object proceed(ProceedingJoinPoint joinPoint) throws Throwable {
        // System.out.println("Aspect executed");
        try {
            DataSourceTypeThreadLocal.set(DataSourceType.READONLY);
            return joinPoint.proceed();
        } finally {
            DataSourceTypeThreadLocal.reset();
        }
    }
}
