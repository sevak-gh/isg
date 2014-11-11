package com.infotech.isg.service;

import com.infotech.isg.domain.Operator;
import com.infotech.isg.repository.OperatorRepository;
import com.infotech.isg.repository.PaymentChannelRepository;

/**
* validating MTN service request.
*
* @author Sevak Gharibian
*/
public class RequestValidatorMTN extends RequestValidator {

    public RequestValidatorMTN(OperatorRepository operatorRepository, PaymentChannelRepository paymentChannelRepository) {
        this.operatorRepository = operatorRepository;
        this.paymentChannelRepository = paymentChannelRepository;
        this.operatorId = Operator.MTN_ID;
    }

    @Override
    protected int validateAmount(int amount) {
        return (amount >= 10000) ? ErrorCodes.OK : ErrorCodes.INVALID_AMOUNT;
    }

    @Override
    protected int validateCellNumber(String cellNumber) {
        if (!(cellNumber.startsWith("93")
              || cellNumber.startsWith("093")
              || cellNumber.startsWith("9893")
              || cellNumber.startsWith("+9893")
              || cellNumber.startsWith("009893")
              || cellNumber.startsWith("94")
              || cellNumber.startsWith("094")
              || cellNumber.startsWith("9894")
              || cellNumber.startsWith("+9894")
              || cellNumber.startsWith("009894"))) {
            return ErrorCodes.INVALID_CELL_NUMBER;
        }
        return ErrorCodes.OK;
    }
}
