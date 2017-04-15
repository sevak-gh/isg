package com.infotech.isg.service.impl;

import com.infotech.isg.service.OperatorService;
import com.infotech.isg.service.OperatorServiceResponse;
import com.infotech.isg.service.OperatorNotAvailableException;
import com.infotech.isg.service.OperatorUnknownResponseException;
import com.infotech.isg.proxy.ProxyAccessException;
import com.infotech.isg.proxy.mcipinless.MCIPinLessProxy;
import com.infotech.isg.proxy.mcipinless.MCIPinLessProxyImpl;
import com.infotech.isg.proxy.mcipinless.MCIPinLessProxyGetTokenResponse;
import com.infotech.isg.proxy.mcipinless.MCIPinLessProxyCallSaleProviderResponse;
import com.infotech.isg.proxy.mcipinless.MCIPinLessProxyExecSaleProviderResponse;

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
 * implementation for MCIPinLess operaotr service.
 *
 * @author Sevak Gharibian
 */
@Service("MCIPinLessOperatorService")
public class MCIPinLessOperatorServiceImpl implements OperatorService {

    private static final Logger LOG = LoggerFactory.getLogger(MCIPinLessOperatorServiceImpl.class);

    @Value("${mcipinless.url}")
    private String url;

    @Value("${mcipinless.username}")
    private String username;

    @Value("${mcipinless.password}")
    private String password;

    @Value("${mcipinless.namespace}")
    private String namespace;

    @Value("${mcipinless.timeout}")
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
            LOG.error("invalid mcipinless timeout param, using default 2000");
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
            throw new OperatorUnknownResponseException("interrupted, recharge response is ambiguous from MCIPinLess, set for STF");
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
            throw new OperatorUnknownResponseException("timeout, recharge response is ambiguous from MCIPinLess, set for STF");
        }    
    }

    @Override
    public OperatorServiceResponse getBill(String consumer) {
        throw new UnsupportedOperationException("pay-bill not defined in MCIPinLess, try jiring");
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

            MCIPinLessProxy mciPinLessProxy = new MCIPinLessProxyImpl(url, username, password, namespace);

            // get token from MCIPinLess
            MCIPinLessProxyGetTokenResponse getTokenResponse = null;
            try {
                getTokenResponse = mciPinLessProxy.getToken();
            } catch (ProxyAccessException e) {
                throw new OperatorNotAvailableException("error in mcipinless GetToken", e);
            }

            String token = getTokenResponse.getToken();
            if (token == null) {
                throw new OperatorNotAvailableException("invalid token from mcipinless");
            }

            // request MCIPinLess to callSaleProvider
            MCIPinLessProxyCallSaleProviderResponse callSaleProviderResponse = null;
            try {
                callSaleProviderResponse = mciPinLessProxy.callSaleProvider(token, consumer, amount);
            } catch (ProxyAccessException e) {
                throw new OperatorNotAvailableException("error in mcipinless CallSaleProvider", e);
            }

            // check callSaleResponse response
            if ((callSaleProviderResponse == null)
                || (callSaleProviderResponse.getResponse() == null)
                || (callSaleProviderResponse.getResponse().size() < 2)
                || (callSaleProviderResponse.getCode() == null)) {
                // invalid response
                throw new OperatorNotAvailableException("invalid mcipinless callSaleProvider response");
            }
            // check recharge capability response
            if (Integer.parseInt(callSaleProviderResponse.getCode()) != 0) {
                OperatorServiceResponse response = new OperatorServiceResponse();
                response.setCode(callSaleProviderResponse.getCode());
                response.setMessage(callSaleProviderResponse.getDetail());
                response.setTransactionId(callSaleProviderResponse.getDetail());
                response.setToken(token);
                return response;
            }

            // get provider Id
            String providerId = callSaleProviderResponse.getDetail();

            // request MCIPinLess to recharge
            MCIPinLessProxyExecSaleProviderResponse execSaleProvider = null;
            try {
                execSaleProvider = mciPinLessProxy.execSaleProvider(token, providerId, "018");
            } catch (ProxyAccessException e) {
                throw new OperatorUnknownResponseException("error in mcipinless execSaleProvider", e);
            }

            // check recharge response
            if ((execSaleProvider == null)
                || (execSaleProvider.getResponse() == null)
                || (execSaleProvider.getResponse().size() < 2)
                || (execSaleProvider.getCode() == null)) {
                // invalid response, should be set for STF
                throw new OperatorUnknownResponseException("execSaleProvider response is ambiguous from MCIPinLess, set for STF");
            }

            // set response, status not exist for MCIPinLess
            OperatorServiceResponse response = new OperatorServiceResponse();
            response.setCode(execSaleProvider.getCode());
            response.setMessage(execSaleProvider.getDetail());
            response.setTransactionId(providerId);
            response.setToken(token);

            return response;
    
        }        
    }
}
