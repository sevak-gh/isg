package com.infotech.isg.service;

import com.infotech.isg.domain.Operator;
import com.infotech.isg.domain.PaymentChannel;
import com.infotech.isg.domain.BankCodes;
import com.infotech.isg.domain.ServiceActions;
import com.infotech.isg.repository.OperatorRepository;
import com.infotech.isg.repository.PaymentChannelRepository;

/**
* validating service request.
*
* @author Sevak Gharibian
*/
public abstract class RequestValidator {

    protected OperatorRepository operatorRepository;
    protected PaymentChannelRepository paymentChannelRepository;
    protected int operatorId;

    public int validate(String action, String bankCode, int amount,
                        int channel, String state, String bankReceipt,
                        String orderId, String consumer, String customerIp) {
        int result;
 
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
            
        return ErrorCodes.OK;
    }

    protected int validateAmount(int amount) {
        if (!((amount == 10000) 
                || (amount == 20000) 
                || (amount == 50000)
                || (amount == 100000) 
                || (amount == 200000))) {
            return ErrorCodes.INVALID_AMOUNT;
        }
        return ErrorCodes.OK;
    }

    protected int validateAction(String action) {        
        return (ServiceActions.isActionExist(action)) ? ErrorCodes.OK : ErrorCodes.INVALID_OPERATOR_ACTION;
    }

    protected int validateCellNumber(String cellNumber) {
        if (!(cellNumber.startsWith("91")
                || cellNumber.startsWith("091")
                || cellNumber.startsWith("9891")
                || cellNumber.startsWith("09891")
                || cellNumber.startsWith("009891"))) {
            return ErrorCodes.INVALID_CELL_NUMBER;
        }
        return ErrorCodes.OK;
    }

    protected int validateBankCode(String bankCode) {
        return (BankCodes.isCodeExist(bankCode)) ? ErrorCodes.OK : ErrorCodes.INVALID_BANK_CODE;
    }

    protected int validateOperator(int operatorId) {
        Operator operator = operatorRepository.findById(operatorId);
        if (operator == null) {
            return ErrorCodes.INVALID_OPERATOR; 
        }
        if (operator.getStatus() != Operator.Status.ACTIVE) {                
            return ErrorCodes.DISABLED_OPERATOR;
        }
        return ErrorCodes.OK;
    }

    protected int validatePaymentChannel(int channelId) {
        PaymentChannel channel = paymentChannelRepository.findById(channelId);
        if (channel == null) {
            return ErrorCodes.INVALID_PAYMENT_CHANNEL; 
        }
        if (!channel.getIsActive()) {                
            return ErrorCodes.DISABLED_PAYMENT_CHANNEL;
        }
        return ErrorCodes.OK;
    }
}
