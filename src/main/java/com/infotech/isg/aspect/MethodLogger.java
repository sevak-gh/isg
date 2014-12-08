package com.infotech.isg.aspect;

import com.infotech.isg.service.ISGServiceResponse;
import com.infotech.isg.service.ISGException;
import com.infotech.isg.validation.ErrorCodes;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Around;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
* generic method entry logger.
*
* @author Sevak Gharibian
*/
@Aspect
@Component
public class MethodLogger {

    private static final Logger LOG = LoggerFactory.getLogger(MethodLogger.class);

    // around execution any public method in ISGServiceImpl class in service package
    @Around("execution(public * com.infotech.isg.service..*.*(..))"
            + " || execution(public * com.infotech.isg.repository..*.*(..))"
            + " || execution(public * com.infotech.isg.validation..*.*(..))"
            + " || execution(public * com.infotech.isg.proxy..*.*(..))")
    public Object logMethodEntry(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        LOG.debug("{}.{}({}): {} in {} msec", joinPoint.getSignature().getDeclaringType().getSimpleName(),
                  joinPoint.getSignature().getName(),
                  joinPoint.getArgs(),
                  result,
                  System.currentTimeMillis() - start);
        return result;
    }
}
