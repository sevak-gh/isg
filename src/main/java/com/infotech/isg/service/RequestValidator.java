package com.infotech.isg.service;

import com.infotech.isg.domain.Transaction;
import com.infotech.isg.domain.Operator;
import com.infotech.isg.domain.PaymentChannel;
import com.infotech.isg.domain.BankCodes;
import com.infotech.isg.domain.ServiceActions;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
* generic service request validator.
*
* @author Sevak Gharibian
*/
public abstract class RequestValidator {

    public int validateRequiredParams(String username, String password, String action,
                                      String bankCode, int amount, int channel,
                                      String state, String bankReceipt, String orderId,
                                      String consumer, String customerIp) {
        if ((username == null)
            || (password == null)
            || (action == null)
            || (bankCode == null)
            || (state == null)
            || (bankReceipt == null)
            || (orderId == null)
            || (consumer == null)
            || (customerIp == null)
            || (state.isEmpty())
            || (bankReceipt.isEmpty())
            || (orderId.isEmpty())
            || (consumer.isEmpty())
            || (customerIp.isEmpty())) {
            return ErrorCodes.INSUFFICIENT_PARAMETERS;
        }

        return ErrorCodes.OK;
    }

    public int validateAmount(int amount) {
        if (!((amount == 10000)
              || (amount == 20000)
              || (amount == 50000)
              || (amount == 100000)
              || (amount == 200000))) {
            return ErrorCodes.INVALID_AMOUNT;
        }
        return ErrorCodes.OK;
    }

    public int validateAction(String action) {
        return (ServiceActions.isActionExist(action)) ? ErrorCodes.OK : ErrorCodes.INVALID_OPERATOR_ACTION;
    }

    public int validateCellNumber(String cellNumber) {
        Pattern pattern = Pattern.compile("^(0|98|\\+98|0098)?91[0-9]{8}$");
        if (!pattern.matcher(cellNumber).matches()) {
            return ErrorCodes.INVALID_CELL_NUMBER;
        }
        return ErrorCodes.OK;
    }

    public int validateBankCode(String bankCode) {
        return (BankCodes.isCodeExist(bankCode)) ? ErrorCodes.OK : ErrorCodes.INVALID_BANK_CODE;
    }

    public int validateOperator(Operator operator) {
        if (operator == null) {
            return ErrorCodes.INVALID_OPERATOR;
        }
        if (!operator.getIsActive()) {
            return ErrorCodes.DISABLED_OPERATOR;
        }
        return ErrorCodes.OK;
    }

    public int validatePaymentChannel(PaymentChannel channel) {
        if (channel == null) {
            return ErrorCodes.INVALID_PAYMENT_CHANNEL;
        }
        if (!channel.getIsActive()) {
            return ErrorCodes.DISABLED_PAYMENT_CHANNEL;
        }
        return ErrorCodes.OK;
    }

    /**
    * checks duplicate transactions.
    */
    public int validateTransaction(Transaction transaction, String orderId,
                                   int operatorId, int amount, int channel,
                                   String consumer, String customerIp) {

        if (transaction == null) {
            return ErrorCodes.OK;
        }

        if (!((transaction.getResNum() != null)
              && (transaction.getResNum().compareTo(orderId) == 0))) {
            // possible fraud
            return ErrorCodes.DOUBLE_SPENDING_TRANSACTION;
        }

        if (!((transaction.getProvider() == operatorId)
              && (transaction.getAmount() == amount)
              && (transaction.getChannel() == channel)
              && ((transaction.getConsumer() != null) && (transaction.getConsumer().compareTo(consumer) == 0))
              && ((transaction.getCustomerIp() != null) && (transaction.getCustomerIp().compareTo(customerIp) == 0)))) {
            return ErrorCodes.DOUBLE_SPENDING_TRANSACTION;
        }

        if (!((transaction.getStatus() != null)
              && (transaction.getStatus() == 1)
              && (transaction.getOperatorResponseCode() != null)
              && (transaction.getOperatorResponseCode() == 0))) {
            return ErrorCodes.TRANSACTION_ALREADY_FAILED;
        }

        if ((transaction.getStf() != null) && (transaction.getStf() == 1)) {
            return ErrorCodes.OPERATOR_SERVICE_ERROR;
        }

        if ((transaction.getStf() != null) && (transaction.getStf() == 3)) {
            return ErrorCodes.STF_ERROR;
        }

        return ErrorCodes.OK;
    }
}
