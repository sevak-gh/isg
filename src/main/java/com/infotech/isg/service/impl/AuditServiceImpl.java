package com.infotech.isg.service.impl;

import com.infotech.isg.service.AuditService;
import com.infotech.isg.domain.Audit;
import com.infotech.isg.repository.AuditLogRepository;

import java.util.Date;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * audit log implementation
 *
 * @author Sevak Gharibian
 */
@Service
public class AuditServiceImpl implements AuditService {

    private final AuditLogRepository auditLogRepository;

    @Autowired
    public AuditServiceImpl(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Override
    @Transactional
    public void log(String username, String bankCode, String amount,
                    String channel, String state, String bankReceipt,
                    String orderId, String consumer, String customerIp,
                    String remoteIp, String action, int operatorId,
                    String status, long isgDoc, String oprDoc,
                    Date timestamp, long responseTime) {

        Audit audit = new Audit();
        audit.setUsername(username);
        audit.setBankCode(bankCode);
        audit.setAmount(amount);
        audit.setChannel(channel);
        audit.setState(state);
        audit.setBankReceipt(bankReceipt);
        audit.setOrderId(orderId);
        audit.setConsumer(consumer);
        audit.setCustomerIp(customerIp);
        audit.setRemoteIp(remoteIp);
        audit.setAction(action);
        audit.setOperatorId(operatorId);
        audit.setStatus(status);
        audit.setIsgDoc(isgDoc);
        audit.setOprDoc(oprDoc);
        audit.setTimestamp(timestamp);
        audit.setResponseTime(responseTime);
        auditLogRepository.save(audit);
   }
}
