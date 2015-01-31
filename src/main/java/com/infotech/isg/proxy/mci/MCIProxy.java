package com.infotech.isg.proxy.mci;

/**
 * proxy client for MCI service.
 *
 * @author Sevak Gharibian
 */
public interface MCIProxy {
    public MCIProxyGetTokenResponse getToken();
    public MCIProxyRechargeResponse recharge(String token, String consumer, int amount, long trId);
    public MCIProxyRechargeVerifyResponse rechargeVerify(String token, String consumer, long trId);
    public MCIProxyGetRemainedBrokerRechargeResponse getRemainedBrokerRecharge(String token, int amount);
}
