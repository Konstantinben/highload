package com.sonet.core.logging.aspect;

import com.sonet.core.logging.LogContext;
import org.aspectj.lang.ProceedingJoinPoint;

public interface ILoggerAspect {

    Object proceedWithLogAround(ProceedingJoinPoint joinPoint, LogContext logContext) throws Throwable;

}
