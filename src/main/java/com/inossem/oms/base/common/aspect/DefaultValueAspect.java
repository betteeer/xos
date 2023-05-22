package com.inossem.oms.base.common.aspect;

import com.inossem.oms.base.common.annotation.DefaultValue;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.FieldSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

@Aspect
@Component
public class DefaultValueAspect {

    @Around("@annotation(com.inossem.oms.base.common.annotation.DefaultValue) && args(defaultValue)")
    public Object aroundSetField(ProceedingJoinPoint joinPoint, DefaultValue defaultValue) throws Throwable {
        System.out.println("Setting field to " + defaultValue);
        // 获取目标对象和目标字段
        Object target = joinPoint.getTarget();
        String fieldName = joinPoint.getSignature().getName().substring(3);
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);

        // 输出日志
        System.out.println("Accessing field " + field.getName());

        // 执行原始的方法调用
        return joinPoint.proceed();
    }
//    @Around("execution(* *(..)) && @annotation(defaultValue)")
//    public Object setDefaultValue(ProceedingJoinPoint joinPoint, DefaultValue defaultValue) throws Throwable {
//        System.out.println("Entering DefaultValueAspect.setDefaultValue()");
//        Object target = joinPoint.getTarget();
//        Field field = getField(joinPoint);
//        field.setAccessible(true);
//        if (field.get(target) == null) {
//            field.set(target, defaultValue.value());
//        }
//        return joinPoint.proceed();
//    }

    private Field getField(ProceedingJoinPoint joinPoint) throws NoSuchFieldException {
        Signature signature = joinPoint.getSignature();
        if (!(signature instanceof FieldSignature)) {
            throw new IllegalArgumentException("This annotation is only applicable to fields");
        }
        FieldSignature fieldSignature = (FieldSignature) signature;
        return fieldSignature.getField();
    }
}