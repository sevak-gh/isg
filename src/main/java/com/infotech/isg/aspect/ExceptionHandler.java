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
* generic exception handler for uncaught exceptions.
*
* @author Sevak Gharibian
*/
@Aspect
@Component
public class ExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ExceptionHandler.class);

    // around execution any public method in ISGService API
    @Around("execution(public * com.infotech.isg.service.ISGService.*(..))")
    public Object translateException(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = null;
        try {
            result = joinPoint.proceed();
        } catch (RuntimeException e) {
            LOG.error("internal error handling service request, internal_system_error code returned", e);
            return new ISGServiceResponse("ERROR", ErrorCodes.INTERNAL_SYSTEM_ERROR, "");
        }
        return result;
    }
}
