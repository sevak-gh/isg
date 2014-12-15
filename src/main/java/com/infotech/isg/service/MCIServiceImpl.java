package com.infotech.isg.service;

import java.util.List;
import java.util.Date;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;

import com.infotech.isg.domain.Operator;
import com.infotech.isg.domain.PaymentChannel;
import com.infotech.isg.domain.Transaction;
import com.infotech.isg.domain.ServiceActions;
import com.infotech.isg.validation.RequestValidator;
import com.infotech.isg.validation.ErrorCodes;
import com.infotech.isg.repository.TransactionRepository;
import com.infotech.isg.proxy.mci.MCIProxy;
import com.infotech.isg.proxy.mci.MCIProxyImpl;
import com.infotech.isg.proxy.mci.MCIProxyRechargeResponse;
import com.infotech.isg.proxy.mci.MCIProxyGetTokenResponse;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
* MCI service implementation.
*
* @author Sevak Gharibian
*/
@Service("MCIServie")
public class MCIServiceImpl implements MCIService {

    private static final Logger LOG = LoggerFactory.getLogger(MCIServiceImpl.class);

    private final AccessControl accessControl;
    private final TransactionRepository transactionRepository;
    private final MCIProxy mciProxy;
    private final RequestValidator mciValidator;

    @Autowired
    public MCIServiceImpl(AccessControl accessControl,
                          @Qualifier("JdbcTransactionRepository") TransactionRepository transactionRepository,
                          MCIProxy mciProxy,
                          @Qualifier("MCIValidator") RequestValidator mciValidator) {
        this.accessControl = accessControl;
        this.transactionRepository = transactionRepository;
        this.mciProxy = mciProxy;
        this.mciValidator = mciValidator;
    }

    @Override
    public ISGServiceResponse mci(String username, String password,
                                  String bankCode, int amount,
                                  int channel, String state,
                                  String bankReceipt, String orderId,
                                  String consumer, String customerIp,
                                  String remoteIp) {

        int operatorId = Operator.MCI_ID;
        int action = ServiceActions.TOP_UP;
        String actionName = "top-up";
        int errorCode = ErrorCodes.OK;

        // check for required params
        errorCode = mciValidator.validateRequiredParams(username, password, actionName,
                    bankCode, amount, channel,
                    state, bankReceipt, orderId,
                    consumer, customerIp);
        if (errorCode != ErrorCodes.OK) {
            return new ISGServiceResponse("ERROR", errorCode, null);
        }

        // validate amount
        errorCode = mciValidator.validateAmount(amount);
        if (errorCode != ErrorCodes.OK) {
            return new ISGServiceResponse("ERROR", errorCode, null);
        }

        // validate cell number
        errorCode = mciValidator.validateCellNumber(consumer);
        if (errorCode != ErrorCodes.OK) {
            return new ISGServiceResponse("ERROR", errorCode, null);
        }

        // validate bank code
        errorCode = mciValidator.validateBankCode(bankCode);
        if (errorCode != ErrorCodes.OK) {
            return new ISGServiceResponse("ERROR", errorCode, null);
        }

        // check if this operator is valid
        errorCode = mciValidator.validateOperator(operatorId);
        if (errorCode != ErrorCodes.OK) {
            return new ISGServiceResponse("ERROR", errorCode, null);
        }

        // validate payment channel
        errorCode = mciValidator.validatePaymentChannel(channel);
        if (errorCode != ErrorCodes.OK) {
            return new ISGServiceResponse("ERROR", errorCode, null);
        }

        // authenticate client
        errorCode = accessControl.authenticate(username, password, remoteIp);
        if (errorCode != ErrorCodes.OK) {
            return new ISGServiceResponse("ERROR", errorCode, null);
        }

        // validate if transaction is duplicate
        List<Transaction> transactions = transactionRepository.findByRefNumBankCodeClientId(bankReceipt, bankCode, accessControl.getClient().getId());
        for (Transaction transaction : transactions) {
            errorCode = mciValidator.validateTransaction(transaction, orderId,
                        operatorId, amount, channel,
                        consumer, customerIp);
            if (errorCode != ErrorCodes.OK) {
                // TODO: may need more review
                switch (errorCode) {
                    case ErrorCodes.STF_RESOLVED_SUCCESSFUL:
                        // STF has resolved this transaction as successful
                        return new ISGServiceResponse("OK", transaction.getId(), transaction.getOperatorResponse());

                    case ErrorCodes.STF_RESOLVED_FAILED:
                        // STF has resolved this transaction as failed
                        return new ISGServiceResponse("ERROR", ErrorCodes.OPERATOR_SERVICE_RESPONSE_NOK, null);

                    case ErrorCodes.STF_ERROR:
                        // invalid STF status, set for STF to try again
                        transaction.setStf(1);
                        transaction.setStfResult(0);
                        transaction.setOperatorResponseCode(2);
                        transactionRepository.update(transaction);
                        return new ISGServiceResponse("ERROR", ErrorCodes.OPERATOR_SERVICE_ERROR_DONOT_REVERSE, null);

                    default:
                        return new ISGServiceResponse("ERROR", errorCode, null);
                }
            }
        }

        // register ongoing transaction
        Transaction transaction = new Transaction();
        transaction.setProvider(operatorId);
        transaction.setAction(action);
        transaction.setState(state);
        transaction.setResNum(orderId);
        transaction.setRefNum(bankReceipt);
        transaction.setRemoteIp(remoteIp);
        transaction.setTrDateTime(new Date());
        transaction.setAmount(amount);
        transaction.setChannel(channel);
        transaction.setConsumer(consumer);
        transaction.setBankCode(bankCode);
        transaction.setClientId(accessControl.getClient().getId());
        transaction.setCustomerIp(customerIp);
        transaction.setStatus(-1);
        transaction.setBankVerify(amount);
        transaction.setVerifyDateTime(new Date());
        transactionRepository.create(transaction);

        // get token from MCI
        MCIProxyGetTokenResponse getTokenResponse = null;
        try {
            getTokenResponse = mciProxy.getToken();
        } catch (ISGException e) {
            LOG.error("error calling mci get token, operator_service_error code returned", e);
            return new ISGServiceResponse("ERROR", ErrorCodes.OPERATOR_SERVICE_ERROR, null);
        }
        if (getTokenResponse == null) {
            return new ISGServiceResponse("ERROR", ErrorCodes.OPERATOR_SERVICE_ERROR, null);
        }
        String token = getTokenResponse.getToken();
        if (token == null) {
            return new ISGServiceResponse("ERROR", ErrorCodes.OPERATOR_SERVICE_ERROR, null);
        }

        // request MCI to recharge
        MCIProxyRechargeResponse rechargeResponse = null;
        try {
            rechargeResponse =  mciProxy.recharge(token,
                                                  consumer,
                                                  amount,
                                                  transaction.getId());
        } catch (ISGException e) {
            // something failed, set for STF
            transaction.setStf(1);
            transaction.setStfResult(0);
            transaction.setOperatorResponseCode(2);
            transactionRepository.update(transaction);
            LOG.error("error calling mci recharge, STF set and operator_service_error_donot_reverse code returned", e);
            return new ISGServiceResponse("ERROR", ErrorCodes.OPERATOR_SERVICE_ERROR_DONOT_REVERSE, null);
        }

        // check recharge response
        if ((rechargeResponse == null)
            || (rechargeResponse.getResponse() == null)
            || (rechargeResponse.getResponse().size() < 2)
            || (rechargeResponse.getCode() == null)) {
            // invalid response, set for STF
            transaction.setStf(1);
            transaction.setStfResult(0);
            transaction.setOperatorResponseCode(2);
            transactionRepository.update(transaction);
            return new ISGServiceResponse("ERROR", ErrorCodes.OPERATOR_SERVICE_ERROR_DONOT_REVERSE, null);
        }

        if (rechargeResponse.getCode().compareToIgnoreCase("0") != 0) {
            // recharge was not successful
            transaction.setStatus(-1);
            transaction.setOperatorDateTime(new Date());
            transaction.setOperatorResponseCode(Integer.valueOf(rechargeResponse.getCode()));
            transaction.setOperatorResponse(rechargeResponse.getDetail());
            transaction.setToken(token);
            transaction.setOperatorTId(rechargeResponse.getDetail());
            transactionRepository.update(transaction);
            return new ISGServiceResponse("ERROR", ErrorCodes.OPERATOR_SERVICE_RESPONSE_NOK, null);
        }

        // recharge was successful, OK
        transaction.setStatus(1);
        transaction.setOperatorDateTime(new Date());
        transaction.setOperatorResponseCode(Integer.valueOf(rechargeResponse.getCode()));
        transaction.setOperatorResponse(rechargeResponse.getDetail());
        transaction.setToken(token);
        transaction.setOperatorTId(rechargeResponse.getDetail());
        transactionRepository.update(transaction);
        return new ISGServiceResponse("OK", transaction.getId(), rechargeResponse.getDetail());
    }
}
