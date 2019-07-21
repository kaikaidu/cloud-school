package com.amway.acti.base.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Parameter;
import java.util.UUID;

@Aspect
@Component
@Slf4j
public class LogAspect {
    @Around("within(com.amway.acti.controller..*) && @annotation(org.springframework.web.bind.annotation.RequestMapping)")
    public Object log(ProceedingJoinPoint joinPoint) throws Throwable {
        String peerId = UUID.randomUUID().toString().replaceAll("-", "");
        String methodName = joinPoint.getSignature().getName();
        Parameter[] parameters = ((MethodSignature) joinPoint.getSignature()).getMethod().getParameters();
        Object[] args = joinPoint.getArgs();
        StringBuilder arguments = new StringBuilder("[");
        for (int i = 0; i < parameters.length; i++) {
            arguments.append(pawrameters[i].getName()).append(":").append(args[i]).append(", ");
        }
        if (parameters.length > 0) {
            int length = arguments.length();
            arguments.delete(length - 2, length);
        }
        arguments.append("]");
        log.info("request_id->{},method->{},arguments->{}", peerId, methodName, arguments);
        Object retVal = null;
        try {
            retVal = joinPoint.proceed();
        } catch (Exception ex) {
            log.info("request_id->{},method->{}\n{}\n\tat {}", peerId, methodName, ex.toString(), ex.getStackTrace()[0]);
            throw ex;
        }
        log.info("request_id->{},method->{},result->{}", peerId, methodName, retVal);
        return retVal;
    }
}
