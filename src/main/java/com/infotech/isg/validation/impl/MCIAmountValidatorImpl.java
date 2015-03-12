package com.infotech.isg.validation.impl;

import com.infotech.isg.domain.ServiceActions;
import com.infotech.isg.validation.AmountValidator;
import com.infotech.isg.validation.ErrorCodes;

import org.springframework.stereotype.Component;

/**
 * MCI validator for amount
 *
 * @author Sevak Gharibian
 */
@Component("MCIAmountValidator")
public class MCIAmountValidatorImpl implements AmountValidator {

    @Override
    public int validate(int amount, int action) {

        if (action == ServiceActions.PAY_BILL) {
            // no amount validation for bill
            return ErrorCodes.OK;
        }

        if (!((amount == 10000)
              || (amount == 20000)
              || (amount == 50000)
              || (amount == 100000)
              || (amount == 200000)
              || (amount == 500000)
              || (amount == 1000000))) {
            return ErrorCodes.INVALID_AMOUNT;
        }
        return ErrorCodes.OK;
    }
}
