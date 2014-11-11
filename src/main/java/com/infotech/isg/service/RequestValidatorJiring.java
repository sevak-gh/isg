package com.infotech.isg.service;

import com.infotech.isg.domain.Operator;
import com.infotech.isg.repository.OperatorRepository;
import com.infotech.isg.repository.PaymentChannelRepository;

/**
* validating Jiring service request.
*
* @author Sevak Gharibian
*/
public class RequestValidatorJiring extends RequestValidator {

    public RequestValidatorJiring(OperatorRepository operatorRepository, PaymentChannelRepository paymentChannelRepository) {
        this.operatorRepository = operatorRepository;
        this.paymentChannelRepository = paymentChannelRepository;
        this.operatorId = Operator.JIRING_ID;
    }

    protected int validateAmount(int amount) {
        return ErrorCodes.OK;
    }

    protected int validateCellNumber(String consumer) {
        return ErrorCodes.OK;
    }

    protected int validateBankCode(String bankCode) {
        return ErrorCodes.OK;
    }

    protected int validateOperator(int operatorId) {
        return ErrorCodes.OK;
    }

    protected int validatePaymentChannel(int channelId) {
        return ErrorCodes.OK;
    }
}

