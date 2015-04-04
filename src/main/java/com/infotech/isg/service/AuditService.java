package com.infotech.isg.service;

import java.util.Date;

/**
 * providing audit log service
 *
 * @author Sevak Gharibian
 */
public interface AuditService {
    public void log(String username, String bankCode, String amount,
                    String channel, String state, String bankReceipt,
                    String orderId, String consumer, String customerIp,
                    String remoteIp, String action, int operatorId,
                    String status, long isgDoc, String oprDoc,
                    Date timestamp, long responseTime);
}
