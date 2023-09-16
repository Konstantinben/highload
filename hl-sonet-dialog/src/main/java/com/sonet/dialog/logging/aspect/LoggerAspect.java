package com.sonet.dialog.logging.aspect;

import com.sonet.dialog.logging.LogContext;
import com.sonet.dialog.logging.RequestDirection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author oracle
 */
@Component
@Slf4j(topic = "Tracer")
@RequiredArgsConstructor
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
class LoggerAspect implements ILoggerAspect {

    protected static final String LOG_MESSAGE_FORMAT = "[{} | {}] {} {} | {} | {} DATA: {} {}";

    public Object proceedWithLogAround(ProceedingJoinPoint joinPoint, LogContext logContext) throws Throwable {
        postProcessLogContext(logContext);

        logBefore(logContext);
        Object result = joinPoint.proceed();
        logAfter(logContext, result);

        return result;
    }

    private void postProcessLogContext(LogContext logContext) {
        if (logContext.getRequestDirection() == RequestDirection.OUTGOING) {
            return;
        }

        if (logContext.getRequestMethod() == null) {
            logContext.setRequestMethod(getCurrentRequestMethod());
        }

        if (logContext.getRequestHeaders() == null) {
            logContext.setRequestHeaders(getCurrentRequestHeaders());
        }
    }

    private void logBefore(LogContext logContext) {
        log.trace(LOG_MESSAGE_FORMAT, logContext.getLogPrefixes().getLeft(), logContext.getTraceId(),
                logContext.getRequestMethod(), logContext.getRequestPath(), logContext.getJavaMethodSignature(),
                logContext.getLogDescriptions().getLeft(), logContext.getRequestParams(),
                "| HEADERS=[" + logContext.getRequestHeaders() + "]");
    }

    private void logAfter(LogContext logContext, Object result) {
        log.trace(LOG_MESSAGE_FORMAT, logContext.getLogPrefixes().getRight(), logContext.getTraceId(),
                logContext.getRequestMethod(), logContext.getRequestPath(), logContext.getJavaMethodSignature(),
                logContext.getLogDescriptions().getRight(), "response=[" + result + "]", "");
    }

    public RequestMethod getCurrentRequestMethod() {
        HttpServletRequest request = getCurrentRequest();
        return RequestMethod.valueOf(request.getMethod());
    }

    public Map<String, List<String>> getCurrentRequestHeaders() {
        HttpServletRequest request = getCurrentRequest();

        return Collections.list(request.getHeaderNames()).stream()
                .collect(Collectors.groupingBy(Function.identity()));
    }

    private static HttpServletRequest getCurrentRequest() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        return Optional.ofNullable(requestAttributes).map(ServletRequestAttributes::getRequest)
                .orElseThrow(() -> new RuntimeException("Cannot provide request"));
    }

}
