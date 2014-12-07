package com.infotech.isg.it.fake.mci;

import com.infotech.isg.proxy.mci.MCIProxyRechargeResponse;
import com.infotech.isg.proxy.mci.MCIProxyGetTokenResponse;

/**
* MCI web service operations/methods.
*
* @uathor Sevak Gharibian
*/
public interface MCIService {
    public MCIProxyGetTokenResponse getToken();
    public MCIProxyRechargeResponse recharge(String token, String consumer,
            int amount, long trId);
}
