package com.infotech.isg.service.impl;

import com.infotech.isg.domain.ServiceActions;
import com.infotech.isg.service.OperatorService;
import com.infotech.isg.service.OperatorServiceResponse;
import com.infotech.isg.service.OperatorNotAvailableException;
import com.infotech.isg.service.OperatorUnknownResponseException;
import com.infotech.isg.service.ISGException;
import com.infotech.isg.proxy.ProxyAccessException;
import com.infotech.isg.proxy.jiring.JiringProxy;
import com.infotech.isg.proxy.jiring.JiringProxyImpl;
import com.infotech.isg.proxy.jiring.TCSRequest;
import com.infotech.isg.proxy.jiring.TCSResponse;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * implementation for Jiring operator service
 *
 * @author Sevak Gharibian
 */
@Service("JiringOperatorService")
public class JiringOperatorServiceImpl implements OperatorService {

    private static final Logger LOG = LoggerFactory.getLogger(JiringOperatorServiceImpl.class);
    private static final String RECHARGE_BRAND_ID = "52";
    private static final String PAYBILL_BRAND_ID = "47";
    private static final String WALLET_BRAND_ID = "49";

    @Value("${jiring.url}")
    private String url;

    @Value("${jiring.username}")
    private String username;

    @Value("${jiring.password}")
    private String password;

    @Value("${jiring.brand}")
    private String brand;


    @Override
    public OperatorServiceResponse topup(String consumer, int amount, long transactionId, String action, String customerName, String vendor, String channel) {

        JiringProxy jiringProxy = new JiringProxyImpl(url, username, password);

        // normalize consumer/cell-number for jiring
        // 091********
        consumer = "091" + consumer.substring(consumer.length() - 8, consumer.length());

        // convert action into jiring brand ID
        String brandId = "";
        switch (ServiceActions.getActionCode(action)) {
            case ServiceActions.TOP_UP: brandId = RECHARGE_BRAND_ID; break;
            case ServiceActions.PAY_BILL: brandId = PAYBILL_BRAND_ID; break;
            case ServiceActions.WALLET: brandId = WALLET_BRAND_ID; break;
            default: throw new ISGException(String.format("jiring brandId not found for: %s", action));
        }

        // get token from jiring
        TCSResponse response = null;
        try {
            // sender: customerName if wallet, else consumer 
            response = jiringProxy.salesRequest(consumer, amount, brandId, (brandId == WALLET_BRAND_ID) ? customerName : consumer);   
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
            response = jiringProxy.salesRequestExec(token, false);
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

    @Override
    public OperatorServiceResponse getBill(String consumer) {
        JiringProxy jiringProxy = new JiringProxyImpl(url, username, password);

        // normalize consumer/cell-number for jiring
        // 091********
        consumer = "091" + consumer.substring(consumer.length() - 8, consumer.length());

        // get token from jiring
        TCSResponse response = null;
        try {
            // dummy amount for pay-bill check-only action
            response = jiringProxy.salesRequest(consumer, 100, PAYBILL_BRAND_ID, consumer);
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
            response = jiringProxy.salesRequestExec(token, true);
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
        serviceResponse.setParam1(response.getParam1());
        serviceResponse.setParam2(response.getParam2());
        serviceResponse.setParam3(response.getParam3());
        serviceResponse.setParam4(response.getParam4());
        return serviceResponse;
    }
}
