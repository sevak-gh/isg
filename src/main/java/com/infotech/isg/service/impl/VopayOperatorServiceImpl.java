package com.infotech.isg.service.impl;

import com.infotech.isg.service.OperatorService;
import com.infotech.isg.service.OperatorServiceResponse;
import com.infotech.isg.service.OperatorNotAvailableException;
import com.infotech.isg.service.OperatorUnknownResponseException;
import com.infotech.isg.proxy.ProxyAccessException;
import com.infotech.isg.proxy.vopay.VopayProxy;
import com.infotech.isg.proxy.vopay.VopayProxyImpl;
import com.infotech.isg.proxy.vopay.VopayProxyAccountInfoResponse;
import com.infotech.isg.proxy.vopay.VopayProxyAvailablePackagesResponse;
import com.infotech.isg.proxy.vopay.VopayProxyPerformTransactionResponse;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * implementation for Vopay operaotr service.
 *
 * @author Sevak Gharibian
 */
@Service("VopayOperatorService")
public class VopayOperatorServiceImpl implements OperatorService {

    private static final Logger LOG = LoggerFactory.getLogger(VopayOperatorServiceImpl.class);

    @Value("${vopay.url}")
    private String url;

    @Value("${vopay.accountId}")
    private String accountId;

    @Value("${vopay.agentId}")
    private String agentId;

    @Value("${vopay.key}")
    private String key;

    @Value("${vopay.secret}")
    private String secret;

    @Value("${vopay.authorizedIp}")
    private String authorizedIp;

    @Override
    public OperatorServiceResponse topup(String consumer, int amount, long transactionId, String action, String customerName, String vendor, String channel) {

        VopayProxy vopayProxy = new VopayProxyImpl(url, accountId, agentId, key, secret, authorizedIp);

        // request Vopay to perform charge transaction
        VopayProxyPerformTransactionResponse performTransactionResponse = null;
        try {
            performTransactionResponse = vopayProxy.performTransaction(consumer, action, vendor, "", "", "");
        } catch (ProxyAccessException e) {
            throw new OperatorUnknownResponseException("error in vopay perform_transction", e);
        }

        // check perform_transaction response
        if ((performTransactionResponse == null)
            || (performTransactionResponse.getSuccess() == null)
            || (performTransactionResponse.getTransactionId() == null)) {
            // invalid response, should be set for STF
            throw new OperatorUnknownResponseException("perform_transaction response is ambiguous from Vopay, set for STF");
        }

        // set response for Vopay
        OperatorServiceResponse response = new OperatorServiceResponse();
        response.setCode((performTransactionResponse.getSuccess()) ? "0" : "1");
        response.setMessage(performTransactionResponse.getErrorMessage());
        response.setTransactionId(String.valueOf(performTransactionResponse.getTransactionId()));

        return response;
    }

    @Override
    public OperatorServiceResponse getBill(String consumer) {
        throw new UnsupportedOperationException("pay-bill not defined in Vopay");
    }
}
