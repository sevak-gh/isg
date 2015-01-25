package com.infotech.isg.proxy.jiring;

import com.infotech.isg.proxy.ServiceProvider;
import com.infotech.isg.proxy.ServiceProviderResponse;
import com.infotech.isg.proxy.OperatorNotAvailableException;
import com.infotech.isg.proxy.OperatorUnknownResponseException;
import com.infotech.isg.proxy.ProxyAccessException;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * implementation for Jiring service provider.
 *
 * @author Sevak Gharibian
 */
@Component("JiringServiceProvider")
public class JiringServiceProviderImpl implements ServiceProvider {

    private static final Logger LOG = LoggerFactory.getLogger(JiringServiceProviderImpl.class);

    private final JiringProxy jiringProxy;

    @Autowired
    public JiringServiceProviderImpl(JiringProxy jiringProxy) {
        this.jiringProxy = jiringProxy;
    }

    @Override
    public ServiceProviderResponse topup(String consumer, int amount, long transactionId) {

        // get token from jiring
        TCSResponse response = null;
        try {
            response = jiringProxy.salesRequest(consumer, amount);
        } catch (ProxyAccessException e) {
            throw new OperatorNotAvailableException("error in jiring SalesRequest", e);
        }

        if ((response == null)
            || (response.getResult() == null)) {
            throw new OperatorNotAvailableException("invalid SalesRequest response from jiring");
        }

        if (!response.getResult().equalsIgnoreCase("0")) {
            // operator responded NOK, token not available
            // set response, status not exist for Jiring
            ServiceProviderResponse serviceResponse = new ServiceProviderResponse();
            serviceResponse.setCode(response.getResult());
            serviceResponse.setMessage(response.getMessage());
            serviceResponse.setTransactionId(response.getParam1());
            return serviceResponse;
        }

        if (response.getParam1() == null) {
            throw new OperatorNotAvailableException("invalid token, SalesRequest response from jiring");
        }

        String token = response.getParam1();
        response = null;
        try {
            response = jiringProxy.salesRequestExec(token);
        } catch (ProxyAccessException e) {
            throw new OperatorUnknownResponseException("error in jiring SalesRequestExec, ambiguous result", e);
        }

        if ((response == null)
            || (response.getResult() == null)) {
            // invalid response, should be set for STF
            throw new OperatorUnknownResponseException("SalesRequestExec response is ambiguous from Jiring, set for STF");
        }

        // set response, status not exist for Jiring
        ServiceProviderResponse serviceResponse = new ServiceProviderResponse();
        serviceResponse.setCode(response.getResult());
        serviceResponse.setMessage(response.getMessage());
        serviceResponse.setTransactionId(response.getParam1());
        serviceResponse.setToken(token);
        return serviceResponse;
    }
}
