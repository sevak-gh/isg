package com.infotech.isg.service;

/**
 * generic ISG serive API
 *
 * @author Sevak Gahribian
 */
public interface ISGService {
    public ISGServiceResponse topup(String username, String password,
                                    String bankCode, int amount,
                                    String channel, String state,
                                    String bankReceipt, String orderId,
                                    String consumer, String customerIp,
                                    String remoteIp, String action);

    public ISGServiceResponse isOperatorAvailable();
    public int getOperatorId();
}
