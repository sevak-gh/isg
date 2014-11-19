package com.infotech.isg.proxy;

/**
* proxy client for MCI service.
*
* @author Sevak Gharibian
*/
public interface MCIService {
    public MCIGetTokenResponse getToken();
    public MCIRechargeResponse recharge(String token, String username, String password,
                                        String consumer, int amount, long trId);
}
