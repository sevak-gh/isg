package com.infotech.isg.repository;

import java.util.Date;

/**
* repository for audit log
*
* @author Sevak Gharibian
*/
public interface AuditLogRepository {
    public void create(String username, String bankCode, int amount,
                       int channel, String state, String bankReceipt,
                       String orderId, String consumer, String customerIp,
                       String remoteIp, String action, int operatorId,
                       String status, long isgDoc, String oprDoc,
                       Date timestamp, long responseTime);
}
