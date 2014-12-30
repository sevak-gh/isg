package com.infotech.isg.validation.impl;

import com.infotech.isg.validation.IRequestValidator;
import com.infotech.isg.validation.AmountValidator;
import com.infotech.isg.validation.CellNumberValidator;
import com.infotech.isg.validation.ActionValidator;
import com.infotech.isg.validation.BankCodeValidator;
import com.infotech.isg.validation.OperatorValidator;
import com.infotech.isg.validation.PaymentChannelValidator;
import com.infotech.isg.validation.ErrorCodes;

/**
* generic request validator.
*
* @author Sevak Gharibian
*/
public abstract class RequestValidatorImpl implements IRequestValidator {

    protected AmountValidator amountValidator;
    protected CellNumberValidator cellNumberValidator;
    protected ActionValidator actionValidator;
    protected BankCodeValidator bankCodeValidator;
    protected OperatorValidator operatorValidator;
    protected PaymentChannelValidator paymentChannelValidator;

    @Override
    public int validate(String username, String password,
                        String bankCode, int amount,
                        int channelId, String state,
                        String bankReceipt, String orderId,
                        String consumer, String customerIp,
                        String remoteIp, String action,
                        int operatorId) {

        int errorCode = ErrorCodes.OK;

        // check required values
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

        // validate amount
        errorCode = amountValidator.validate(amount);
        if (errorCode != ErrorCodes.OK) {
            return errorCode;
        }

        // validate cell number
        errorCode = cellNumberValidator.validate(consumer);
        if (errorCode != ErrorCodes.OK) {
            return errorCode;
        }

        // validate bank code
        errorCode = bankCodeValidator.validate(bankCode);
        if (errorCode != ErrorCodes.OK) {
            return errorCode;
        }

        // check if this operator is valid
        errorCode = operatorValidator.validate(operatorId);
        if (errorCode != ErrorCodes.OK) {
            return errorCode;
        }

        // validate payment channel
        errorCode = paymentChannelValidator.validate(channelId);
        if (errorCode != ErrorCodes.OK) {
            return errorCode;
        }

        return ErrorCodes.OK;
    }
}
