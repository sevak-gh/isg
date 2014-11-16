package com.infotech.isg.service;

import com.infotech.isg.domain.Client;
import com.infotech.isg.domain.Transaction;
import com.infotech.isg.domain.Operator;
import com.infotech.isg.domain.PaymentChannel;
import com.infotech.isg.domain.BankCodes;
import com.infotech.isg.domain.ServiceActions;
import com.infotech.isg.repository.OperatorRepository;
import com.infotech.isg.repository.PaymentChannelRepository;
import com.infotech.isg.repository.ClientRepository;
import com.infotech.isg.repository.TransactionRepository;
import com.infotech.isg.util.HashGenerator;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
* validating service request.
*
* @author Sevak Gharibian
*/
public abstract class RequestValidator {

    protected OperatorRepository operatorRepository;
    protected PaymentChannelRepository paymentChannelRepository;
    protected ClientRepository clientRepository;
    protected TransactionRepository transactionRepository;
    protected int operatorId;

    public int validate(String username, String password, String action,
                        String bankCode, int amount, int channel,
                        String state, String bankReceipt, String orderId,
                        String consumer, String customerIp, String remoteIp) {
        int result;

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

        Client client = clientRepository.findByUsername(username);

        result = validateClient(username, password, remoteIp);
        if (result != ErrorCodes.OK) {
            return result;
        }

        result = validateAction(action);
        if (result != ErrorCodes.OK) {
            return result;
        }

        result = validateAmount(amount);
        if (result != ErrorCodes.OK) {
            return result;
        }

        result = validateCellNumber(consumer);
        if (result != ErrorCodes.OK) {
            return result;
        }

        result = validateBankCode(bankCode);
        if (result != ErrorCodes.OK) {
            return result;
        }

        result = validateOperator(operatorId);
        if (result != ErrorCodes.OK) {
            return result;
        }

        result = validatePaymentChannel(channel);
        if (result != ErrorCodes.OK) {
            return result;
        }

        result = validateTransaction(bankReceipt, bankCode, client.getId(), orderId, amount,
                                     channel, consumer, customerIp);
        if (result != ErrorCodes.OK) {
            return result;
        }

        return ErrorCodes.OK;
    }

    public int validateClient(String username, String password, String remoteIp) {
        Client client = clientRepository.findByUsername(username);
        if (client == null) {
            return ErrorCodes.INVALID_USERNAME_OR_PASSWORD;
        }
        if (!client.getPassword().equalsIgnoreCase(HashGenerator.getSHA512(password))) {
            return ErrorCodes.INVALID_USERNAME_OR_PASSWORD;
        }
        if (!client.getIsActive()) {
            return ErrorCodes.DISABLED_CLIENT_ACCOUNT;
        }
        if (!client.getIps().contains(remoteIp)) {
            return ErrorCodes.INVALID_CLIENT_IP;
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

    public int validateOperator(int operatorId) {
        Operator operator = operatorRepository.findById(operatorId);
        if (operator == null) {
            return ErrorCodes.INVALID_OPERATOR;
        }
        if (!operator.getIsActive()) {
            return ErrorCodes.DISABLED_OPERATOR;
        }
        return ErrorCodes.OK;
    }

    public int validatePaymentChannel(int channelId) {
        PaymentChannel channel = paymentChannelRepository.findById(Integer.toString(channelId));
        if (channel == null) {
            return ErrorCodes.INVALID_PAYMENT_CHANNEL;
        }
        if (!channel.getIsActive()) {
            return ErrorCodes.DISABLED_PAYMENT_CHANNEL;
        }
        return ErrorCodes.OK;
    }

    public int validateTransaction(String refNum, String bankCode, int clientId,
                                   String orderId, int amount, int channel,
                                   String consumer, String customerIp) {
        Transaction transaction = transactionRepository.findByRefNumBankCodeClientId(refNum, bankCode, clientId);

        if (transaction.getResNum() != orderId) {
            // possible fraud
            return ErrorCodes.DOUBLE_SPENDING_TRANSACTION;
        }

        if (!((transaction.getProvider() == operatorId)
              && (transaction.getAmount() == amount)
              && (transaction.getChannel() == channel)
              && (transaction.getConsumer() == consumer)
              && (transaction.getCustomerIp() == customerIp))) {
            return ErrorCodes.DOUBLE_SPENDING_TRANSACTION;
        }

        if ((transaction.getStatus() == null)
            || (transaction.getStatus().intValue() != 1)
            || (transaction.getOperatorResponseCode() == 0)
            || (transaction.getOperatorResponseCode().intValue() != 0)) {
            return ErrorCodes.OPERATOR_SERVICE_UNAVAILABLE;
        }

        if ((transaction.getStf() != null) && (transaction.getStf().intValue() == 1)) {
            return ErrorCodes.OPERATOR_SERVICE_ERROR;
        }

        if ((transaction.getStf() != null) && (transaction.getStf().intValue() == 3)) {
            return ErrorCodes.INVALID_CELL_NUMBER;
        }

        return ErrorCodes.OK;
    }

}
