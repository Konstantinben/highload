package com.sonet.core.logging.aspect;

import com.sonet.core.logging.RequestDirection;
import com.sonet.core.logging.RequestLoggingManager;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

//@Aspect
@Component
@RequiredArgsConstructor
public class FeignClientAspect {

    private final Environment env;

    protected final RequestLoggingManager requestLoggingManager;

    @Pointcut("@within(org.springframework.cloud.openfeign.FeignClient)")
    public void withinFeignClient() {}

    @Pointcut("withinFeignClient()")
    public void targetPointcut() {}

    @Around("targetPointcut() && @annotation(mapping)")
    public Object getMapping(ProceedingJoinPoint joinPoint, GetMapping mapping) {
        return proceed(joinPoint, RequestMethod.GET, mapping.value());
    }

    @Around("targetPointcut() && @annotation(mapping)")
    public Object postMapping(ProceedingJoinPoint joinPoint, PostMapping mapping) {
        return proceed(joinPoint, RequestMethod.POST, mapping.value());
    }

    @Around("targetPointcut() && @annotation(mapping)")
    public Object putMapping(ProceedingJoinPoint joinPoint, PutMapping mapping) {
        return proceed(joinPoint, RequestMethod.PUT, mapping.value());
    }

    @Around("targetPointcut() && @annotation(mapping)")
    public Object deleteMapping(ProceedingJoinPoint joinPoint, DeleteMapping mapping) {
        return proceed(joinPoint, RequestMethod.DELETE, mapping.value());
    }

    private Object proceed(ProceedingJoinPoint joinPoint, RequestMethod method, String[] paths) {
        List<String> pathParts = new ArrayList<>();
        pathParts.add(getFeignClientBaseUrl(joinPoint));

        if (paths.length > 0) {
            pathParts.add(String.join("/", paths));
        }

        String path = String.join("/", pathParts);

        return requestLoggingManager.proceed(joinPoint, RequestDirection.OUTGOING,
            method, path, "CLIENT_SEND", "CLIENT_RECEIVE");
    }

    private String getFeignClientBaseUrl(ProceedingJoinPoint joinPoint) {
        FeignClient feignClient = AopUtils.findTargetClassAnnotation(joinPoint, FeignClient.class);

        if (feignClient != null) {
            return getEnvPropertyValue(feignClient.url());
        }

        return "";
    }

    private String getEnvPropertyValue(String propertyName) {
        return env.getProperty(propertyName.replaceAll("\\$|\\{|}", ""), propertyName);
    }

}
