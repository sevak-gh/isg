package com.infotech.isg.service;

/**
* provides MCI serive operations.
*
* @author Sevak Gahribian
*/
public interface MCIService {
    public ISGServiceResponse mci(String username, String password,
                                  String bankCode, int amount,
                                  int channel, String state,
                                  String bankReceipt, String orderId,
                                  String consumer, String customerIp,
                                  String remoteIp);
}
