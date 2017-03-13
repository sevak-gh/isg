package com.infotech.isg.service.impl;

import com.infotech.isg.service.OperatorService;
import com.infotech.isg.service.OperatorServiceResponse;
import com.infotech.isg.service.OperatorNotAvailableException;
import com.infotech.isg.service.OperatorUnknownResponseException;
import com.infotech.isg.proxy.ProxyAccessException;
import com.infotech.isg.proxy.mci.MCIProxy;
import com.infotech.isg.proxy.mci.MCIProxyImpl;
import com.infotech.isg.proxy.mci.MCIProxyRechargeResponse;
import com.infotech.isg.proxy.mci.MCIProxyRechargeCapabilityResponse;
import com.infotech.isg.proxy.mci.MCIProxyGetTokenResponse;

import java.util.concurrent.Future;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Callable;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * implementation for MCI operaotr service.
 *
 * @author Sevak Gharibian
 */
@Service("MCIOperatorService")
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

    @Value("${mci2.url}")
    private String url2;

    @Value("${mci2.username}")
    private String username2;

    @Value("${mci2.password}")
    private String password2;

    @Value("${mci2.namespace}")
    private String namespace2;

    @Value("${mci.timeout}")
    private String timeout;

    private static ExecutorService executor = Executors.newCachedThreadPool();

    @Override
    public OperatorServiceResponse topup(String consumer, int amount, 
                                            long transactionId, String action, 
                                            String customerName, String vendor, 
                                            String channel, String clientUsername) {

        CallableTopup callableTopup = new CallableTopup(consumer, amount, transactionId, action, customerName, vendor, channel);
        Future<OperatorServiceResponse> future = executor.submit(callableTopup);
    
        int timeoutMillis = 2000;
        try {
            timeoutMillis = Integer.parseInt(timeout);
        } catch (NumberFormatException e) {
            // invalid timeout param value, use the default
            LOG.error("invalid mci timeout param, using default 2000");
        }

        if (clientUsername.equals("customersoap")) {
            timeoutMillis = 30000; // 30 sec fixed for customersoap, they do not support -10, do_not_reverse
        }

        try {
            OperatorServiceResponse response  = future.get(timeoutMillis, TimeUnit.MILLISECONDS);
            return response;
        } catch (InterruptedException e ) {
            // async job interrupted, operator result unknown
            // throw unknown result, this will be set for STF, switch should recheck later
            throw new OperatorUnknownResponseException("interrupted, recharge response is ambiguous from MCI, set for STF");
        } catch (ExecutionException e ) {
            // async job threw exception, rethrow the same exception
            if (e.getCause() instanceof OperatorNotAvailableException) {
                throw (OperatorNotAvailableException)e.getCause();
            } else if (e.getCause() instanceof OperatorUnknownResponseException) {
                throw (OperatorUnknownResponseException)e.getCause();
            } else {
                throw new RuntimeException(e.getCause());
            }                 
        } catch (TimeoutException e ) {
            // async job not completed yet
            // throw unknown result, this will be set for STF, switch should recheck later
            throw new OperatorUnknownResponseException("timeout, recharge response is ambiguous from MCI, set for STF");
        }    
    }

    @Override
    public OperatorServiceResponse getBill(String consumer) {
        throw new UnsupportedOperationException("pay-bill not defined in MCI, try jiring");
    }


    class CallableTopup implements Callable<OperatorServiceResponse> {

        private final String consumer; 
        private final int amount; 
        private final long transactionId; 
        private final String action; 
        private final String customerName; 
        private final String vendor; 
        private final String channel;

        public CallableTopup(String consumer, int amount, 
                             long transactionId, String action, 
                             String customerName, String vendor, 
                             String channel) {
            this.consumer = consumer;
            this.amount = amount;
            this.transactionId = transactionId;
            this.action = action;
            this.customerName = customerName;
            this.vendor = vendor;
            this.channel = channel;
        }

        @Override
        public OperatorServiceResponse call() throws Exception {
            return doTopup();
        }

        private OperatorServiceResponse doTopup() {

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

            /*
            MCIProxy mciProxy2 = new MCIProxyImpl(url2, username2, password2, namespace2);

            // check MCI recharge capability
            MCIProxyRechargeCapabilityResponse rechargeCapabilityResponse = null;
            try {
                rechargeCapabilityResponse = mciProxy2.rechargeCapability(token, consumer, amount, transactionId);
            } catch (ProxyAccessException e) {
                throw new OperatorNotAvailableException("error in mci recharge capability", e);
            }

            // check recharge capability response
            if ((rechargeCapabilityResponse == null)
                || (rechargeCapabilityResponse.getResponse() == null)
                || (rechargeCapabilityResponse.getResponse().size() < 2)
                || (rechargeCapabilityResponse.getCode() == null)) {
                // invalid response
                throw new OperatorNotAvailableException("invalid mci recharge capability response");
            }

            // check recharge capability response
            if ((Integer.parseInt(rechargeCapabilityResponse.getCode()) > 0)
                || (Integer.parseInt(rechargeCapabilityResponse.getCode()) < -1017)
                || ((Integer.parseInt(rechargeCapabilityResponse.getCode()) > -1001) && (Integer.parseInt(rechargeCapabilityResponse.getCode()) < -1))) {
                // invalid response code
                throw new OperatorNotAvailableException("invalid mci recharge capability response");
            }

            // check recharge capability response code            
            if (Integer.parseInt(rechargeCapabilityResponse.getCode()) != 0) {
                // set response, status not exist for MCI
                OperatorServiceResponse response = new OperatorServiceResponse();
                response.setCode(rechargeCapabilityResponse.getCode());
                response.setMessage(rechargeCapabilityResponse.getDetail());
                response.setTransactionId(rechargeCapabilityResponse.getDetail());
                response.setToken(token);
                return response;
            }
            */

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

            // MCI error codes list changing all the time, error code validation seems useless
            /*
            if ((Integer.parseInt(rechargeResponse.getCode()) > 0)
                || (Integer.parseInt(rechargeResponse.getCode()) < -1017)
                || ((Integer.parseInt(rechargeResponse.getCode()) > -1001) && (Integer.parseInt(rechargeResponse.getCode()) < -1))) {
                // invalid response code, should be set for STF
                throw new OperatorUnknownResponseException("recharge response code is ambiguous from MCI, set for STF");
            }
            */

            // set response, status not exist for MCI
            OperatorServiceResponse response = new OperatorServiceResponse();
            response.setCode(rechargeResponse.getCode());
            response.setMessage(rechargeResponse.getDetail());
            response.setTransactionId(rechargeResponse.getDetail());
            response.setToken(token);

            return response;
    
        }        
    }
}
