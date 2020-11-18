package com.example.vtkdemo.logging;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class AccessAspect {
    @Pointcut("within(com.example.vtkdemo.controller..*)")
    public void inControllerPackage() {
//in controller package
    }

    @Pointcut("this(org.springframework.data.repository.Repository)")
    public void inRepositoryInterface() {
//in repository interface
    }

    @Pointcut("execution(public * *(..))")
    public void publicMethod() {
//public method
    }

    @Pointcut("execution(* *.*(..))")
    public void allMethods() {
// all methods
    }

    @Pointcut("@annotation(org.springframework.web.bind.annotation.GetMapping) || "
            + "@annotation(org.springframework.web.bind.annotation.PostMapping) || "
            + "@annotation(org.springframework.web.bind.annotation.PutMapping) || "
            + "@annotation(org.springframework.web.bind.annotation.DeleteMapping)")
    public void isRequestMapping() {
//is request mapping
    }

    @Before("inControllerPackage() && isRequestMapping() && publicMethod()")
    public void setControllerAndMethodNameInContext(JoinPoint joinPoint) {
        setLoggingContext(joinPoint);
    }

    @Before("inRepositoryInterface() && allMethods()")
    public void setRepositoryAndMethodNameInContext(JoinPoint joinPoint) {
        setLoggingContext(joinPoint);
    }

    private void setLoggingContext(JoinPoint joinPoint) {
        Signature signature = joinPoint.getSignature();
        String controllerName = signature.getDeclaringType().getSimpleName();
        String controllerMethodName = signature.getName();
        LoggingContext.setControllerName(controllerName);
        LoggingContext.setControllerMethodName(controllerMethodName);
    }
}
