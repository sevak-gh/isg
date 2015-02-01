package com.infotech.isg.service.impl;

import com.infotech.isg.service.OperatorService;
import com.infotech.isg.service.OperatorServiceResponse;
import com.infotech.isg.service.OperatorNotAvailableException;
import com.infotech.isg.service.OperatorUnknownResponseException;
import com.infotech.isg.proxy.ProxyAccessException;
import com.infotech.isg.proxy.jiring.JiringProxy;
import com.infotech.isg.proxy.jiring.JiringProxyImpl;
import com.infotech.isg.proxy.jiring.TCSRequest;
import com.infotech.isg.proxy.jiring.TCSResponse;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * implementation for Jiring operator service
 *
 * @author Sevak Gharibian
 */
@Component("JiringOperatorService")
public class JiringOperatorServiceImpl implements OperatorService {

    private static final Logger LOG = LoggerFactory.getLogger(JiringOperatorServiceImpl.class);

    @Value("${jiring.url}")
    private String url;

    @Value("${jiring.username}")
    private String username;

    @Value("${jiring.password}")
    private String password;

    @Value("${jiring.brand}")
    private String brand;


    @Override
    public OperatorServiceResponse topup(String consumer, int amount, long transactionId) {

        JiringProxy jiringProxy = new JiringProxyImpl(url, username, password, brand);

        // normalize consumer/cell-number for jiring
        // 091********
        consumer = "091" + consumer.substring(consumer.length() - 8, consumer.length());

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
            OperatorServiceResponse serviceResponse = new OperatorServiceResponse();
            serviceResponse.setCode(response.getResult());
            serviceResponse.setMessage(response.getMessage());
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
        OperatorServiceResponse serviceResponse = new OperatorServiceResponse();
        serviceResponse.setCode(response.getResult());
        serviceResponse.setMessage(response.getMessage());
        serviceResponse.setTransactionId(response.getParam1());
        serviceResponse.setToken(token);
        return serviceResponse;
    }
}
