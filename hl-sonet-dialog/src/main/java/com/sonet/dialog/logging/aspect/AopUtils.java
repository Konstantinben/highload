package com.sonet.dialog.logging.aspect;

import lombok.experimental.UtilityClass;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.lang.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

@UtilityClass
public class AopUtils {

    @Nullable
    public <T extends Annotation> T findTargetClassAnnotation(JoinPoint joinPoint, Class<T> annotationClass) {
        return getTargetClass(joinPoint).getAnnotation(annotationClass);
    }

    public Class<?> getTargetClass(JoinPoint joinPoint) {
        return getTargetMethod(joinPoint).getDeclaringClass();
    }

    public Method getTargetMethod(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        return signature.getMethod();
    }

    public String getMethodSignature(JoinPoint joinPoint) {
        return getTargetClass(joinPoint).getSimpleName() + "." + getTargetMethod(joinPoint).getName();
    }
}
