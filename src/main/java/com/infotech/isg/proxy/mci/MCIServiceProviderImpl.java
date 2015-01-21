package com.infotech.isg.proxy.mci;

import com.infotech.isg.proxy.ServiceProvider;
import com.infotech.isg.proxy.ServiceProviderResponse;
import com.infotech.isg.proxy.OperatorNotAvailableException;
import com.infotech.isg.proxy.OperatorUnknownResponseException;
import com.infotech.isg.proxy.ProxyAccessException;
import com.infotech.isg.proxy.mci.MCIProxy;
import com.infotech.isg.proxy.mci.MCIProxyRechargeResponse;
import com.infotech.isg.proxy.mci.MCIProxyGetTokenResponse;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * implementation for MCI service provider.
 *
 * @author Sevak Gharibian
 */
@Component("MCIServiceProvider")
public class MCIServiceProviderImpl implements ServiceProvider {

    private static final Logger LOG = LoggerFactory.getLogger(MCIServiceProviderImpl.class);

    private final MCIProxy mciProxy;

    @Autowired
    public MCIServiceProviderImpl(MCIProxy mciProxy) {
        this.mciProxy = mciProxy;
    }

    @Override
    public ServiceProviderResponse topup(String consumer, int amount, long transactionId) {

        // get token from MCI
        MCIProxyGetTokenResponse getTokenResponse = null;
        try {
            getTokenResponse = mciProxy.getToken();
        } catch (ProxyAccessException e) {
            throw new OperatorNotAvailableException("error in mci GetToken", e);
        }

        String token = getTokenResponse.getToken();
        if (token == null) {
            throw new OperatorNotAvailableException("invalid token from mci");
        }

        // request MCI to recharge
        MCIProxyRechargeResponse rechargeResponse = null;
        try {
            rechargeResponse = mciProxy.recharge(token, consumer, amount, transactionId);
        } catch (ProxyAccessException e) {
            throw new OperatorUnknownResponseException("error in mci Recharge", e);
        }

        // check recharge response
        if ((rechargeResponse == null)
            || (rechargeResponse.getResponse() == null)
            || (rechargeResponse.getResponse().size() < 2)
            || (rechargeResponse.getCode() == null)) {
            // invalid response, should be set for STF
            throw new OperatorUnknownResponseException("recharge response is ambiguous from MCI, set for STF");
        }

        // set response, status not exist for MCI
        ServiceProviderResponse response = new ServiceProviderResponse();
        response.setCode(rechargeResponse.getCode());
        response.setMessage(rechargeResponse.getDetail());
        response.setTransactionId(token);

        return response;
    }
}
