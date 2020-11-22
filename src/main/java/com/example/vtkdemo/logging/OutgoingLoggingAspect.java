package com.example.vtkdemo.logging;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
public class OutgoingLoggingAspect {

    @Pointcut("@annotation(com.example.vtkdemo.logging.EnableOutgoingLogging)")
    public void enableOutgoingLogging() {
        // enable outgoing logging annotation
    }

    @Around("enableOutgoingLogging()")
    public Object timeAnnotatedServices(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        boolean isSuccess = true;

        try {
            return proceedingJoinPoint.proceed();
        } catch (Throwable t) {
            isSuccess = false;
            throw t;
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            Signature signature = proceedingJoinPoint.getSignature();
            String className = signature.getDeclaringType().getSimpleName();
            String eventName = signature.getName();

            log.info("service={} eventName={} success={} duration={} logType=outgoing", className, eventName, isSuccess, duration);
        }
    }
}
