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
                                    String remoteIp, String action, String customerName);

    public ISGServiceResponse getBill(String consumer);
    public ISGServiceResponse isOperatorAvailable();
    public ISGServiceResponse verifyTransaction(String consumer, String transactionId);
    public int getOperatorId();
}
