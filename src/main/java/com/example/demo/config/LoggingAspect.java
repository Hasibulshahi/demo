package com.example.demo.config;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;

@Aspect
@Component
public class LoggingAspect {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Pointcut("within(com.example.demo.service..*)")
    public void serviceLayer() {}

    @Before("serviceLayer()")
    public void logBefore(JoinPoint joinPoint) {
        logger.info("Entering: {} with args {}", joinPoint.getSignature(), joinPoint.getArgs());
    }

    @AfterReturning(value = "serviceLayer()", returning = "result")
    public void logAfter(JoinPoint joinPoint, Object result) {
        logger.info("Exiting: {} with result {}", joinPoint.getSignature(), result);
    }
}
