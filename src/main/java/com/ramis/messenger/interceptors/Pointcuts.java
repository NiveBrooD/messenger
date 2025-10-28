package com.ramis.messenger.interceptors;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class Pointcuts {

    @Pointcut("within(com.ramis.messenger..*Controller)")
    public void isController() {
    }


    @Pointcut("@within(org.springframework.stereotype.Controller)")
    public void isControllerAnno() {
    }
}
