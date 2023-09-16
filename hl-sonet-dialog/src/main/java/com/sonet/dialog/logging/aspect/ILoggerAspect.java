package com.sonet.dialog.logging.aspect;

import com.sonet.dialog.logging.LogContext;
import org.aspectj.lang.ProceedingJoinPoint;

public interface ILoggerAspect {

    Object proceedWithLogAround(ProceedingJoinPoint joinPoint, LogContext logContext) throws Throwable;

}
