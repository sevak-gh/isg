package com.infotech.isg.service;

import com.infotech.isg.domain.Operator;
import com.infotech.isg.repository.OperatorRepository;
import com.infotech.isg.repository.PaymentChannelRepository;
import com.infotech.isg.repository.ClientRepository;
import com.infotech.isg.repository.TransactionRepository;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
* validating MTN service request.
*
* @author Sevak Gharibian
*/
public class RequestValidatorMTN extends RequestValidator {

    public RequestValidatorMTN(OperatorRepository operatorRepository, PaymentChannelRepository paymentChannelRepository,
                               ClientRepository clientRepository, TransactionRepository transactionRepository) {
        this.operatorRepository = operatorRepository;
        this.paymentChannelRepository = paymentChannelRepository;
        this.clientRepository = clientRepository;
        this.transactionRepository = transactionRepository;
        this.operatorId = Operator.MTN_ID;
    }

    @Override
    public int validateAmount(int amount) {
        return (amount >= 10000) ? ErrorCodes.OK : ErrorCodes.INVALID_AMOUNT;
    }

    @Override
    public int validateCellNumber(String cellNumber) {
        Pattern pattern = Pattern.compile("^(0|98|\\+98|0098)?9[34][0-9]{8}$");
        if (!pattern.matcher(cellNumber).matches()) {
            return ErrorCodes.INVALID_CELL_NUMBER;
        }
        return ErrorCodes.OK;
    }
}
