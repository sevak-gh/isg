package com.infotech.isg.validation;

/**
 * ISG service request validator.
 *
 * @author Sevak Gharibian
 */
public interface RequestValidator {
    public int validate(String username, String password,
                        String bankCode, int amount,
                        int channelId, String state,
                        String bankReceipt, String orderId,
                        String consumer, String customerIp,
                        String remoteIp, String action,
                        int operatorId);
}
