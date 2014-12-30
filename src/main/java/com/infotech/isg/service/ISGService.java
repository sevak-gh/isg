package com.infotech.isg.service;

/**
* provides ISG serive operations.
*
* @author Sevak Gahribian
*/
public interface ISGService {
    public ISGServiceResponse topup(String username, String password,
                                    String bankCode, int amount,
                                    int channel, String state,
                                    String bankReceipt, String orderId,
                                    String consumer, String customerIp,
                                    String remoteIp, String action);
}
