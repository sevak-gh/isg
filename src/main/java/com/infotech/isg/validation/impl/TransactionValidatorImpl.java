package com.infotech.isg.validation.impl;

import com.infotech.isg.validation.TransactionValidator;
import com.infotech.isg.domain.Transaction;
import com.infotech.isg.repository.TransactionRepository;
import com.infotech.isg.validation.ErrorCodes;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
* validator for request transaction.
*
* @author Sevak Gharibian
*/
@Component("TransactionValidator")
public class TransactionValidatorImpl implements TransactionValidator {

    private final TransactionRepository transactionRepository;

    @Autowired
    public TransactionValidatorImpl(@Qualifier("JdbcTransactionRepository") TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public int validate(String bankReceipt, String bankCode, int clientId,
                        String orderId, int operatorId, int amount,
                        int channelId, String consumer, String customerIp) {

        List<Transaction> transactions = transactionRepository.findByRefNumBankCodeClientId(bankReceipt, bankCode, clientId);
        for (Transaction transaction : transactions) {

            if (!((transaction.getProvider() == operatorId)
                  && (transaction.getAmount() == amount)
                  && (transaction.getChannel() == channelId)
                  && ((transaction.getResNum() != null)  && transaction.getResNum().equals(orderId))
                  && ((transaction.getConsumer() != null) && transaction.getConsumer().equals(consumer))
                  && ((transaction.getCustomerIp() != null) && transaction.getCustomerIp().equals(customerIp)))) {
                // possible fraud
                return ErrorCodes.DOUBLE_SPENDING_TRANSACTION;
            }

            if (transaction.getStf() == null) {
                // not set for STF, means already handled
                return ErrorCodes.REPETITIVE_TRANSACTION;
            }

            if (transaction.getStf() == 1) {
                // STF not resolved yet
                return ErrorCodes.OPERATOR_SERVICE_ERROR_DONOT_REVERSE;
            }

            if (transaction.getStf() == 3) {
                // STF resolved to FAILED
                return ErrorCodes.STF_RESOLVED_FAILED;
            }

            if (transaction.getStf() == 2) {
                // STF resolved to SUCCESSFUL
                return ErrorCodes.STF_RESOLVED_SUCCESSFUL;
            }

            // invalid STF value
            return ErrorCodes.STF_ERROR;
        }

        // transaction not tried before
        return ErrorCodes.OK;
    }
}
