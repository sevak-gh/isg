package com.infotech.isg.validation;

import com.infotech.isg.repository.OperatorRepository;
import com.infotech.isg.repository.PaymentChannelRepository;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;


/**
* validating MTN service request.
*
* @author Sevak Gharibian
*/
@Component("MTNValidator")
public class MTNRequestValidator extends RequestValidator {
    @Autowired
    public MTNRequestValidator(@Qualifier("JdbcOperatorRepository") OperatorRepository operatorRepository,
                               @Qualifier("JdbcPaymentChannelRepository") PaymentChannelRepository paymentChannelRepository) {
        super(operatorRepository, paymentChannelRepository);
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
