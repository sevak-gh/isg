package com.infotech.isg.aspect;

import com.infotech.isg.domain.Operator;
import com.infotech.isg.validation.ErrorCodes;
import com.infotech.isg.service.ISGService;
import com.infotech.isg.service.ISGServiceResponse;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Around;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
* audit logger for requests
*
* @author Sevak Gharibian
*/
@Aspect
@Component
public class AuditLogger {

    private static final Logger LOG = LoggerFactory.getLogger("isg.audit");

    // around execution ISGService API
    @Around("execution(public * com.infotech.isg.service.ISGService.topup(..))")
    public Object auditLog(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        ISGServiceResponse response = (com.infotech.isg.service.ISGServiceResponse) result;
        LOG.info("\u001B[32m{}\u001B[0m {} for [{},{}] from [{},'{}',{}] => [{}{}\u001B[0m,{}({}),{}] in {} msec",
                 Operator.getName(((com.infotech.isg.service.ISGService)joinPoint.getThis()).getOperatorId()),  // operator name
                 joinPoint.getArgs()[11].toString(),                                                            // action name
                 joinPoint.getArgs()[8].toString(),                                                             // consumer
                 joinPoint.getArgs()[3].toString(),                                                             // amount
                 joinPoint.getArgs()[0].toString(),                                                             // username
                 joinPoint.getArgs()[10].toString(),                                                            // remote Ip
                 joinPoint.getArgs()[4].toString(),                                                             // channel
                 (response.getStatus().equals("OK")) ? "\u001B[32m" : "\u001B[31m", response.getStatus(),
                 ErrorCodes.toString((int)response.getISGDoc()), response.getISGDoc(), response.getOPRDoc(), (System.currentTimeMillis() - start));
        return result;
    }
}
