package com.infotech.isg.validation.impl;

import com.infotech.isg.validation.AmountValidator;
import com.infotech.isg.validation.ErrorCodes;

import org.springframework.stereotype.Component;

/**
 * Generic validator for amount
 *
 * @author Sevak Gharibian
 */
@Component("GenericAmountValidator")
public class GenericAmountValidatorImpl implements AmountValidator {

    @Override
    public int validate(int amount, int action) {
        return (amount >= 1000) ? ErrorCodes.OK : ErrorCodes.INVALID_AMOUNT;
    }
}
