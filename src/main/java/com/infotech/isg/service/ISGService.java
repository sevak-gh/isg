package com.infotech.isg.service;

/**
* provides ISG serive main functionalities.
*
* @atuhor Sevak Gahribian
*/
public interface ISGService {
    public ISGServiceResponse mci(String username, String password,
                                  String bankCode, int amount,
                                  int channel, String state,
                                  String bankReceipt, String orderId,
                                  String consumer, String customerIp);
}
