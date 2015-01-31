package com.infotech.isg.validation;

/**
 * validator for request transactions.
 *
 * @author Sevak Gharibian
 */
public interface TransactionValidator {
    public int validate(String bankReceipt, String bankCode, int clientId,
                        String orderId, int operatorId, int amount,
                        String channelId, String consumer, String customerIp);
}
