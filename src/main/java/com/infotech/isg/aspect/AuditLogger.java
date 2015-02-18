package com.infotech.isg.aspect;

import com.infotech.isg.domain.Operator;
import com.infotech.isg.validation.ErrorCodes;
import com.infotech.isg.service.ISGService;
import com.infotech.isg.service.ISGServiceResponse;
import com.infotech.isg.repository.AuditLogRepository;

import java.util.Date;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Around;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * audit logger for requests
 *
 * @author Sevak Gharibian
 */
@Aspect
@Component
public class AuditLogger {

    private static final Logger LOG = LoggerFactory.getLogger("isg.audit");

    private final AuditLogRepository auditLogRepository;


    @Autowired
    public AuditLogger(@Qualifier("JdbcAuditLogRepository") AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
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
        String username = joinPoint.getArgs()[0].toString();
        String bankCode = joinPoint.getArgs()[2].toString();
        String amount = joinPoint.getArgs()[3].toString();
        String channel = joinPoint.getArgs()[4].toString();
        String state = joinPoint.getArgs()[5].toString();
        String bankReceipt = joinPoint.getArgs()[6].toString();
        String orderId = joinPoint.getArgs()[7].toString();
        String consumer = joinPoint.getArgs()[8].toString();
        String customerIp = joinPoint.getArgs()[9].toString();
        String[] tokens = customerIp.split("-");
        String terminalId = ((tokens != null) && (tokens.length > 0)) ? tokens[0] : null;
        String remoteIp = joinPoint.getArgs()[10].toString();
        String action = joinPoint.getArgs()[11].toString();
        int operatorId = ((com.infotech.isg.service.ISGService)joinPoint.getThis()).getOperatorId();

        // audit log in file
        LOG.info("\u001B[32m{}\u001B[0m {} for [{},{}] from [{},{},{},T({}),RRN({})] => [{}{}\u001B[0m,{}({}),{}{}\u001B[0m] in {} msec",
                 Operator.getName(operatorId),              // operator name
                 action,                                    // action name
                 consumer,                                  // consumer
                 amount,                                    // amount
                 username,                                  // username
                 remoteIp,                                  // remote Ip
                 channel,                                   // channel
                 terminalId,                                // customer IP, first part
                 bankReceipt,                               // RRN, Refnum, bank receipt => unique code from payment switch
                 (response.getStatus().equals("OK")) ? "\u001B[32m" : "\u001B[31m", response.getStatus(),
                 ErrorCodes.toString((int)response.getISGDoc()), response.getISGDoc(),
                 ((response.getOPRDoc() != null) && (response.getOPRDoc().startsWith("-"))) ? "\u001B[31m" : "\u001B[0m", response.getOPRDoc(),
                 responseTime);

        // audit log in DB
        auditLogRepository.create(username, bankCode, amount, channel, state, bankReceipt, orderId,
                                  consumer, customerIp, remoteIp, action, operatorId,
                                  response.getStatus(), response.getISGDoc(), response.getOPRDoc(),
                                  start, responseTime);

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
        auditLogRepository.create(null, null, null, null, null, null, null,
                                  null, null, null, "isAvailable", operatorId,
                                  response.getStatus(), response.getISGDoc(), response.getOPRDoc(),
                                  start, responseTime);

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
        auditLogRepository.create(null, null, null, null, null, null, null,
                                  consumer, null, null, "verify:" + transactionId, operatorId,
                                  response.getStatus(), response.getISGDoc(), response.getOPRDoc(),
                                  start, responseTime);

        return result;
    }
}
