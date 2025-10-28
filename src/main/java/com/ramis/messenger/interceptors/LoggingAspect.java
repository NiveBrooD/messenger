package com.ramis.messenger.interceptors;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Pointcut("execution(* com.ramis.messenger.service.*Service.*(..))")
    public void isServiceMethod() {
    }

    @Pointcut("@within(org.springframework.stereotype.Service) && execution(* *(..))" )
    public void isServiceAnnotatedMethod() {
    }

    @Pointcut("isServiceMethod() || isServiceAnnotatedMethod()")
    public void anyServiceMethod() {
    }

    @Pointcut("execution(* com.ramis.messenger.controller.*Controller.*(..))")
    public void isControllerMethod() {
    }

    @Before("anyServiceMethod() || isControllerMethod()")
    public void startServiceLogging(JoinPoint joinPoint) {
        String shortString = joinPoint.getSignature().toShortString();
        log.info("Method {} called", shortString);
    }

    @After("anyServiceMethod()")
    public void endServiceLogging(JoinPoint joinPoint) {
        log.info("Method {} finished ok", joinPoint.getSignature().toShortString());
    }

    @AfterThrowing(pointcut = "anyServiceMethod()", throwing = "ex")
    public void afterThrowingServiceLogging(JoinPoint joinPoint, Exception ex) {
        log.info("Method {} thrown exception: {}", joinPoint.getSignature().toShortString(), ex.getMessage());
    }

    @AfterReturning(pointcut = "anyServiceMethod()", returning = "result")
    public void afterReturningServiceLogging(JoinPoint joinPoint, Object result) {
        log.info("Method {} returned {}", joinPoint.getSignature().toShortString(), result);
    }

}
