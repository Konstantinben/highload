package com.sonet.core.logging.aspect;

import com.sonet.core.logging.RequestDirection;
import com.sonet.core.logging.RequestLoggingManager;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

//@Aspect
@Component
@RequiredArgsConstructor
public class RestControllerAspect {

    private final RequestLoggingManager requestLoggingManager;

    @Pointcut("@within(org.springframework.web.bind.annotation.RestController)")
    public void withinRestController() {}

    @Pointcut("withinRestController()")
    public void targetPointcut() {}

    @Around("targetPointcut() && @annotation(mapping)")
    public Object requestMapping(ProceedingJoinPoint joinPoint, RequestMapping mapping) {
        return proceed(joinPoint, mapping.value());
    }

    @Around("targetPointcut() && @annotation(mapping)")
    public Object getMapping(ProceedingJoinPoint joinPoint, GetMapping mapping) {
        return proceed(joinPoint, mapping.value());
    }

    @Around("targetPointcut() && @annotation(mapping)")
    public Object postMapping(ProceedingJoinPoint joinPoint, PostMapping mapping) {
        return proceed(joinPoint, mapping.value());
    }

    @Around("targetPointcut() && @annotation(mapping)")
    public Object putMapping(ProceedingJoinPoint joinPoint, PutMapping mapping) {
        return proceed(joinPoint, mapping.value());
    }

    @Around("targetPointcut() && @annotation(mapping)")
    public Object deleteMapping(ProceedingJoinPoint joinPoint, DeleteMapping mapping) {
        return proceed(joinPoint, mapping.value());
    }

    private Object proceed(ProceedingJoinPoint joinPoint, String[] paths) {
        List<String> pathParts = new ArrayList<>();

        RequestMapping classRequestMapping = AopUtils.findTargetClassAnnotation(joinPoint, RequestMapping.class);
        if (classRequestMapping != null) {
            pathParts.add(String.join("/", classRequestMapping.value()));
        }

        if (paths.length > 0) {
            pathParts.add(String.join("/", paths));
        }

        String path = "/" + String.join("/", pathParts);
        
        return requestLoggingManager.proceed(joinPoint, RequestDirection.INCOMING, null,
            path, "ENDPOINT_INPUT", "ENDPOINT_OUTPUT");
    }

}
