package com.infotech.isg.aspect;

import com.infotech.isg.domain.Operator;
import com.infotech.isg.validation.ErrorCodes;
import com.infotech.isg.service.ISGService;
import com.infotech.isg.service.ISGServiceResponse;
import com.infotech.isg.service.AuditService;

import java.util.Date;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Around;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * audit logger for requests
 *
 * @author Sevak Gharibian
 */
@Aspect
@Component
public class AuditLogger {

    private static final Logger LOG = LoggerFactory.getLogger("isg.audit");

    private final AuditService auditService;


    @Autowired
    public AuditLogger(AuditService auditService ) {
        this.auditService = auditService;
    }

    // around execution ISGService API
    @Around("execution(public * com.infotech.isg.service.ISGService.topup(..))")
    public Object auditTopupLog(ProceedingJoinPoint joinPoint) throws Throwable {

        // get start time
        Date start = new Date();

        // invoke the operation
        Object result = joinPoint.proceed();

        long responseTime = System.currentTimeMillis() - start.getTime();

        // get request/response
        ISGServiceResponse response = (com.infotech.isg.service.ISGServiceResponse) result;
        String username = String.valueOf(joinPoint.getArgs()[0]);
        String bankCode = String.valueOf(joinPoint.getArgs()[2]);
        String amount = String.valueOf(joinPoint.getArgs()[3]);
        String channel = String.valueOf(joinPoint.getArgs()[4]);
        String state = String.valueOf(joinPoint.getArgs()[5]);
        String bankReceipt = String.valueOf(joinPoint.getArgs()[6]);
        String orderId = String.valueOf(joinPoint.getArgs()[7]);
        String consumer = String.valueOf(joinPoint.getArgs()[8]);
        String customerIp = String.valueOf(joinPoint.getArgs()[9]);
        String terminalId = null;
        if (customerIp != null) {
            String[] tokens = customerIp.split("-");
            terminalId = ((tokens != null) && (tokens.length > 0)) ? tokens[0] : null;
        }
        String remoteIp = String.valueOf(joinPoint.getArgs()[10]);
        String action = String.valueOf(joinPoint.getArgs()[11]);
        String customerName = String.valueOf(joinPoint.getArgs()[12]);
        String vendor = String.valueOf(joinPoint.getArgs()[13]);
        int operatorId = ((com.infotech.isg.service.ISGService)joinPoint.getThis()).getOperatorId();

        // audit log in file
        LOG.info("\u001B[32m{}\u001B[0m {} for [{},{}] from [{},{},{},T({}),RRN({}),{}] => [{}{}\u001B[0m,{}({}),{}{}\u001B[0m,{}] in {} msec",
                 Operator.getName(operatorId),              // operator name
                 action,                                    // action name
                 consumer,                                  // consumer
                 amount,                                    // amount
                 username,                                  // username
                 remoteIp,                                  // remote Ip
                 channel,                                   // channel
                 terminalId,                                // customer IP, first part
                 bankReceipt,                               // RRN, Refnum, bank receipt => unique code from payment switch
                 vendor,                                    // infotech by default                    
                 (response.getStatus().equals("OK")) ? "\u001B[32m" : "\u001B[31m", response.getStatus(),
                 ErrorCodes.toString((int)response.getISGDoc()), response.getISGDoc(),
                 ((response.getOPRDoc() != null) && (response.getOPRDoc().startsWith("-"))) ? "\u001B[31m" : "\u001B[0m", response.getOPRDoc(),
                 response.getMessage(),
                 responseTime);

        // audit log in DB
        auditService.log(username, bankCode, amount, channel, state, bankReceipt, orderId,
                         consumer, customerIp, remoteIp, action, operatorId,
                         response.getStatus(), response.getISGDoc(), response.getOPRDoc(),
                         start, responseTime, vendor);

        return result;
    }

    @Around("execution(public * com.infotech.isg.service.ISGService.isOperatorAvailable(..))")
    public Object auditIsAvailableLog(ProceedingJoinPoint joinPoint) throws Throwable {

        // get start time
        Date start = new Date();

        // invoke the operation
        Object result = joinPoint.proceed();

        long responseTime = System.currentTimeMillis() - start.getTime();

        // get request/response
        ISGServiceResponse response = (com.infotech.isg.service.ISGServiceResponse) result;
        int operatorId = ((com.infotech.isg.service.ISGService)joinPoint.getThis()).getOperatorId();

        // audit log in file
        LOG.info("\u001B[32m{}\u001B[0m is vailable => [{}{}\u001B[0m,{}({}),{}{}\u001B[0m] in {} msec",
                 Operator.getName(operatorId),              // operator name
                 (response.getStatus().equals("OK")) ? "\u001B[32m" : "\u001B[31m",
                 response.getStatus(),
                 ((int)response.getISGDoc() < 0) ? ErrorCodes.toString((int)response.getISGDoc()) : "",
                 response.getISGDoc(),
                 ((response.getOPRDoc() != null) && (response.getOPRDoc().startsWith("-"))) ? "\u001B[31m" : "\u001B[0m",
                 response.getOPRDoc(),
                 responseTime);

        // audit log in DB
        auditService.log(null, null, null, null, null, null, null,
                         null, null, null, "isAvailable", operatorId,
                         response.getStatus(), response.getISGDoc(), response.getOPRDoc(),
                         start, responseTime, null);

        return result;
    }

    @Around("execution(public * com.infotech.isg.service.ISGService.verifyTransaction(..))")
    public Object auditVerifyLog(ProceedingJoinPoint joinPoint) throws Throwable {

        // get start time
        Date start = new Date();

        // invoke the operation
        Object result = joinPoint.proceed();

        long responseTime = System.currentTimeMillis() - start.getTime();

        // get request/response
        ISGServiceResponse response = (com.infotech.isg.service.ISGServiceResponse) result;
        String consumer = joinPoint.getArgs()[0].toString();
        String transactionId = joinPoint.getArgs()[1].toString();
        int operatorId = ((com.infotech.isg.service.ISGService)joinPoint.getThis()).getOperatorId();

        // audit log in file
        LOG.info("\u001B[32m{}\u001B[0m verify({},{}) => [{}{}\u001B[0m,{}({}),{}{}\u001B[0m] in {} msec",
                 Operator.getName(operatorId),              // operator name
                 consumer,
                 transactionId,
                 (response.getStatus().equals("OK")) ? "\u001B[32m" : "\u001B[31m",
                 response.getStatus(),
                 ((int)response.getISGDoc() < 0) ? ErrorCodes.toString((int)response.getISGDoc()) : "",
                 response.getISGDoc(),
                 ((response.getOPRDoc() != null) && (response.getOPRDoc().startsWith("-"))) ? "\u001B[31m" : "\u001B[0m",
                 response.getOPRDoc(),
                 responseTime);

        // audit log in DB
        auditService.log(null, null, null, null, null, null, null,
                         consumer, null, null, "verify:" + transactionId, operatorId,
                         response.getStatus(), response.getISGDoc(), response.getOPRDoc(),
                         start, responseTime, null);

        return result;
    }

    @Around("execution(public * com.infotech.isg.service.ISGService.getBill(..))")
    public Object auditGetBillLog(ProceedingJoinPoint joinPoint) throws Throwable {

        // get start time
        Date start = new Date();

        // invoke the operation
        Object result = joinPoint.proceed();

        long responseTime = System.currentTimeMillis() - start.getTime();

        // get request/response
        ISGServiceResponse response = (com.infotech.isg.service.ISGServiceResponse) result;
        int operatorId = ((com.infotech.isg.service.ISGService)joinPoint.getThis()).getOperatorId();

        // audit log in file
        LOG.info("\u001B[32m{}\u001B[0m getBill => [{}{}\u001B[0m,{}({}),{}{}\u001B[0m,{}] in {} msec",
                 Operator.getName(operatorId),              // operator name
                 (response.getStatus().equals("OK")) ? "\u001B[32m" : "\u001B[31m",
                 response.getStatus(),
                 ((int)response.getISGDoc() < 0) ? ErrorCodes.toString((int)response.getISGDoc()) : "",
                 response.getISGDoc(),
                 ((response.getOPRDoc() != null) && (response.getOPRDoc().startsWith("-"))) ? "\u001B[31m" : "\u001B[0m",
                 response.getOPRDoc(),
                 response.getMessage(),
                 responseTime);

        // audit log in DB
        auditService.log(null, null, null, null, null, null, null,
                         null, null, null, "getBill", operatorId,
                         response.getStatus(), response.getISGDoc(), response.getOPRDoc(),
                         start, responseTime, null);

        return result;
    }
}
