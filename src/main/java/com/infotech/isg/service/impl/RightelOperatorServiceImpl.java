package com.infotech.isg.service.impl;

import com.infotech.isg.service.OperatorService;
import com.infotech.isg.service.OperatorServiceResponse;
import com.infotech.isg.service.OperatorNotAvailableException;
import com.infotech.isg.service.OperatorUnknownResponseException;
import com.infotech.isg.proxy.ProxyAccessException;
import com.infotech.isg.proxy.rightel.RightelProxy;
import com.infotech.isg.proxy.rightel.RightelProxyImpl;
import com.infotech.isg.proxy.rightel.RightelProxySubmitChargeRequestResponse;
import com.infotech.isg.proxy.rightel.RightelProxyConfirmChargeRequestResponse;
import com.infotech.isg.domain.ServiceActions;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * implementation for Rightel operaotr service.
 *
 * @author Sevak Gharibian
 */
@Service("RightelOperatorService")
public class RightelOperatorServiceImpl implements OperatorService {

    private static final Logger LOG = LoggerFactory.getLogger(RightelOperatorServiceImpl.class);

    @Value("${rightel.url}")
    private String url;

    @Value("${rightel.username}")
    private String username;

    @Value("${rightel.password}")
    private String password;

    @Value("${rightel.namespace}")
    private String namespace;

    @Override
    public OperatorServiceResponse topup(String consumer, int amount, long transactionId, String action, String customerName, String vendor, String channel) {

        RightelProxy rightelProxy = new RightelProxyImpl(url, username, password, namespace);

        // submit charge request
        int actionCode = 0;
        switch (ServiceActions.getActionCode(action)) {
            case ServiceActions.TOP_UP:actionCode=2;break;
            case ServiceActions.WOW:actionCode=3;break;
            default:break;
        }
        RightelProxySubmitChargeRequestResponse submitChargeRequestResponse = null;
        try {
            submitChargeRequestResponse = rightelProxy.submitChargeRequest(consumer, amount, actionCode);
        } catch (ProxyAccessException e) {
            throw new OperatorNotAvailableException("error in rightel SubmitChargeRequestResponse", e);
        }

        if (submitChargeRequestResponse.getErrorCode() != 0) {
            // charge not available, set response
            OperatorServiceResponse response = new OperatorServiceResponse();
            response.setCode(Integer.toString(submitChargeRequestResponse.getErrorCode()));
            response.setMessage(submitChargeRequestResponse.getErrorDesc());
            return response;
        }
        
        if (submitChargeRequestResponse.getRequestId() == null) {
            throw new OperatorNotAvailableException("invalid request idtoken from rightel");
        }

        // confirm charge request
        RightelProxyConfirmChargeRequestResponse confirmChargeRequestResponse = null;
        try {
            confirmChargeRequestResponse = rightelProxy.confirmChargeRequest(submitChargeRequestResponse.getRequestId(), transactionId);
        } catch (ProxyAccessException e) {
            throw new OperatorUnknownResponseException("error in rightel ConfirmChargeRequest", e);
        }

        if (confirmChargeRequestResponse.getErrorCode() != 0) {
            // ambiguous response, should be set for STF
            throw new OperatorUnknownResponseException("ConfirmChargeRequest response is ambiguous from Rightel, set for STF");
        }

        if ((confirmChargeRequestResponse.getStatus() != 6)
            && (confirmChargeRequestResponse.getStatus() != 4)
            && (confirmChargeRequestResponse.getStatus() != 7)) {
            // not final response, should be set for STF
            throw new OperatorUnknownResponseException("ConfirmChargeRequest response is not final from Rightel, set for STF");
        }

        if (confirmChargeRequestResponse.getStatus() == 6) {
            // set response for unsuccessful operation
            OperatorServiceResponse response = new OperatorServiceResponse();
            response.setCode(Integer.toString(confirmChargeRequestResponse.getStatus()));
            response.setMessage(confirmChargeRequestResponse.getChargeResponseDesc());
            response.setTransactionId(confirmChargeRequestResponse.getRequestId());
            response.setToken(submitChargeRequestResponse.getRequestId());
            response.setStatus(Integer.toString(confirmChargeRequestResponse.getStatus()));
            return response;
        }

        // set response for successful operation
        OperatorServiceResponse response = new OperatorServiceResponse();
        response.setCode("0");
        response.setMessage(confirmChargeRequestResponse.getChargeResponseDesc());
        response.setTransactionId(confirmChargeRequestResponse.getRequestId());
        response.setToken(submitChargeRequestResponse.getRequestId());
        response.setStatus(Integer.toString(confirmChargeRequestResponse.getStatus()));
        return response;
    }

    @Override
    public OperatorServiceResponse getBill(String consumer) {
        throw new UnsupportedOperationException("pay-bill not defined in Rightel");
    }
}
