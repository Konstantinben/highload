package com.sonet.core.logging;

import com.sonet.core.logging.aspect.AopUtils;
import com.sonet.core.logging.aspect.ILoggerAspect;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.tuple.Pair;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.CodeSignature;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RequestLoggingManager {

    private final TracerService tracerService;

    private final ILoggerAspect requestLoggerAspectSupport;

    @SneakyThrows
    public Object proceed(ProceedingJoinPoint joinPoint, RequestDirection requestDirection,
                          @Nullable RequestMethod requestMethod, String path, String before, String after) {
        String descriptionBefore = "";
        String descriptionAfter = "";
        Trace trace = AopUtils.getTargetMethod(joinPoint).getAnnotation(Trace.class);
        if (trace != null) {
            if (!trace.inputDescription().isBlank()) {
                descriptionBefore = trace.inputDescription() + " |";
            }

            if (!trace.outputDescription().isBlank()) {
                descriptionAfter = trace.outputDescription() + " |";
            }
        }

        LogContext logContext = LogContext.builder()
            .traceId(tracerService.traceId())
            .requestPath(preparePath(path, joinPoint))
            .requestMethod(requestMethod)
            .requestDirection(requestDirection)
            .javaMethodSignature(AopUtils.getMethodSignature(joinPoint))
            .logPrefixes(Pair.of(before, after))
            .logDescriptions(Pair.of(descriptionBefore, descriptionAfter))
            .requestParams(params(joinPoint))
            .build();

        return requestLoggerAspectSupport.proceedWithLogAround(joinPoint, logContext);
    }

    private List<String> params(JoinPoint joinPoint) {
        List<String> result = new ArrayList<>();
        CodeSignature signature = (CodeSignature) joinPoint.getSignature();
        Object[] args = joinPoint.getArgs();

        Method targetMethod = AopUtils.getTargetMethod(joinPoint);
        Trace trace = targetMethod.getAnnotation(Trace.class);

        for (int i = 0; i < signature.getParameterNames().length; i++) {
            String parameterName = signature.getParameterNames()[i];
            result.add(parameterName + "=" + args[i]);
        }

        return result;
    }

    private String preparePath(String path, JoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Parameter[] parameters = methodSignature.getMethod().getParameters();

        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            PathVariable annotation = parameter.getAnnotation(PathVariable.class);
            if (annotation != null) {
                String pathName = parameter.getName();
                Object arg = joinPoint.getArgs()[i];
                if (!annotation.name().isBlank()) {
                    pathName = annotation.name();
                } else if (!annotation.value().isBlank()) {
                    pathName = annotation.value();
                }

                path = path.replace("{" + pathName + "}", arg.toString());
            }
        }

        return path.replaceAll("(?<!(http:|https:))/{2,}", "/");
    }

}
