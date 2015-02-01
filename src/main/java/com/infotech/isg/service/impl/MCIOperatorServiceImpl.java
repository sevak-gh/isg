package com.infotech.isg.service.impl;

import com.infotech.isg.service.OperatorService;
import com.infotech.isg.service.OperatorServiceResponse;
import com.infotech.isg.service.OperatorNotAvailableException;
import com.infotech.isg.service.OperatorUnknownResponseException;
import com.infotech.isg.proxy.ProxyAccessException;
import com.infotech.isg.proxy.mci.MCIProxy;
import com.infotech.isg.proxy.mci.MCIProxyImpl;
import com.infotech.isg.proxy.mci.MCIProxyRechargeResponse;
import com.infotech.isg.proxy.mci.MCIProxyGetTokenResponse;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * implementation for MCI operaotr service.
 *
 * @author Sevak Gharibian
 */
@Component("MCIOperatorService")
public class MCIOperatorServiceImpl implements OperatorService {

    private static final Logger LOG = LoggerFactory.getLogger(MCIOperatorServiceImpl.class);

    @Value("${mci.url}")
    private String url;

    @Value("${mci.username}")
    private String username;

    @Value("${mci.password}")
    private String password;

    @Value("${mci.namespace}")
    private String namespace;


    @Override
    public OperatorServiceResponse topup(String consumer, int amount, long transactionId) {

        MCIProxy mciProxy = new MCIProxyImpl(url, username, password, namespace);

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

        if ((Integer.parseInt(rechargeResponse.getCode()) > 0)
            || (Integer.parseInt(rechargeResponse.getCode()) < -1017)
            || ((Integer.parseInt(rechargeResponse.getCode()) > -1001) && (Integer.parseInt(rechargeResponse.getCode()) < 0))) {
            // invalid response code, should be set for STF
            throw new OperatorUnknownResponseException("recharge response code is ambiguous from MCI, set for STF");
        }

        // set response, status not exist for MCI
        OperatorServiceResponse response = new OperatorServiceResponse();
        response.setCode(rechargeResponse.getCode());
        response.setMessage(rechargeResponse.getDetail());
        response.setTransactionId(rechargeResponse.getDetail());
        response.setToken(token);

        return response;
    }
}
