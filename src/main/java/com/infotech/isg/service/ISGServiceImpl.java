package com.infotech.isg.service;

import java.util.List;

import com.infotech.isg.domain.Operator;
import com.infotech.isg.domain.PaymentChannel;
import com.infotech.isg.domain.Transaction;
import com.infotech.isg.service.AccessControl;
import com.infotech.isg.repository.OperatorRepository;
import com.infotech.isg.repository.PaymentChannelRepository;
import com.infotech.isg.repository.TransactionRepository;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

/**
* ISG service implementation.
*
* @author Sevak Gharibian
*/
@Service("ISGService")
public class ISGServiceImpl implements ISGService {

    private AccessControl accessControl;
    private OperatorRepository operatorRepository;
    private PaymentChannelRepository paymentChannelRepository;
    private TransactionRepository transactionRepository;

    @Autowired
    public void setAccesControl(AccessControl accessControl) {
        this.accessControl = accessControl;
    }

    @Autowired
    public void setOperatorRepository(OperatorRepository operatorRepository) {
        this.operatorRepository = operatorRepository;
    }

    @Autowired
    public void setPaymentChannelRepository(PaymentChannelRepository paymentChannelRepository) {
        this.paymentChannelRepository = paymentChannelRepository;
    }

    @Autowired
    public void setTransactionRepository(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public ISGServiceResponse mci(String username, String password,
                                  String bankCode, int amount,
                                  int channel, String state,
                                  String bankReceipt, String orderId,
                                  String consumer, String customerIp,
                                  String remoteIp) {

        RequestValidator validator = new MCIRequestValidator();
        int operatorId = Operator.MCI_ID;
        int errorCode = ErrorCodes.OK;

        errorCode = validator.validateRequiredParams(username, password, "top-up",
                                                       bankCode, amount, channel,
                                                       state, bankReceipt, orderId,
                                                       consumer, customerIp);
        if (errorCode != ErrorCodes.OK) {
            // insufficient params
            return new ISGServiceResponse("ERROR", errorCode, null);
        }

        errorCode = validator.validateAmount(amount);
        if (errorCode != ErrorCodes.OK) {
            // invalid amount
            return new ISGServiceResponse("ERROR", errorCode, null);
        }

        errorCode = validator.validateCellNumber(consumer);
        if (errorCode != ErrorCodes.OK) {
            // invalid cell number
            return new ISGServiceResponse("ERROR", errorCode, null);
        }

        errorCode = validator.validateBankCode(bankCode);
        if (errorCode != ErrorCodes.OK) {
            // invalid bank code
            return new ISGServiceResponse("ERROR", errorCode, null);
        }

        errorCode = accessControl.authenticate(username, password, remoteIp);
        if (errorCode != ErrorCodes.OK) {
            // client access denied
            return new ISGServiceResponse("ERROR", errorCode, null);
        }

        Operator operator = operatorRepository.findById(operatorId);
        errorCode = validator.validateOperator(operator);
        if (errorCode != ErrorCodes.OK) {
            // operator not available
            return new ISGServiceResponse("ERROR", errorCode, null);
        }

        PaymentChannel paymentChannel = paymentChannelRepository.findById(Integer.toString(channel));
        errorCode = validator.validatePaymentChannel(paymentChannel);
        if (errorCode != ErrorCodes.OK) {
            // invalid payment channel
            return new ISGServiceResponse("ERROR", errorCode, null);
        }
        
        List<Transaction> transactions = transactionRepository.findByRefNumBankCodeClientId(bankReceipt, bankCode, accessControl.getClient().getId());
        for (Transaction transaction : transactions) {
            errorCode = validator.validateTransaction(transaction, orderId,
                                                        operatorId, amount, channel,
                                                        consumer, customerIp);
            if (errorCode != ErrorCodes.OK) {
                // invalid duplicate transction
                return new ISGServiceResponse("ERROR", errorCode, null);
            }
        }

        return new ISGServiceResponse("OK", 0, "YES");
    }
}
