package com.infotech.isg.validation.impl;

import com.infotech.isg.domain.ServiceActions;
import com.infotech.isg.validation.AmountValidator;
import com.infotech.isg.validation.ErrorCodes;

import org.springframework.stereotype.Component;

/**
 * Rightel validator for amount
 *
 * @author Sevak Gharibian
 */
@Component("RightelAmountValidator")
public class RightelAmountValidatorImpl implements AmountValidator {

    @Override
    public int validate(int amount, int action) {
        switch (action) {
            case ServiceActions.TOP_UP:return ((amount >= 1000) ? ErrorCodes.OK : ErrorCodes.INVALID_AMOUNT);
            case ServiceActions.WOW:
                if ((amount == 20000)
                    || (amount == 50000)
                    || (amount == 100000)
                    || (amount == 200000)
                    || (amount == 500000)
                    || (amount == 1000000)) {
                    return ErrorCodes.OK;
                }
                return ErrorCodes.INVALID_AMOUNT;

            default: return ErrorCodes.OK;                 
        }
    }
}
