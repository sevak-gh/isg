package com.infotech.isg.proxy.mci;

/**
* proxy client for MCI service.
*
* @author Sevak Gharibian
*/
public interface MCIProxy {
    public MCIProxyGetTokenResponse getToken();
    public MCIProxyRechargeResponse recharge(String token, String username, String password,
            String consumer, int amount, long trId);
}
