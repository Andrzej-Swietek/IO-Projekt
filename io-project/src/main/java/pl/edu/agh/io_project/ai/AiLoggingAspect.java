package pl.edu.agh.io_project.ai;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class AiLoggingAspect {

    @Around("execution(* pl.edu.agh.io_project.ai.adapters..*(..))")
    public Object logAiCalls(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object result;
        try {
            result = joinPoint.proceed();
            return result;
        } finally {
            long duration = System.currentTimeMillis() - start;
            log.info("AI Call: {} took {}ms", joinPoint.getSignature(), duration);
        }
    }
}