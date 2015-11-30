package com.infotech.isg.validation.impl;

import com.infotech.isg.domain.ServiceActions;
import com.infotech.isg.validation.AmountValidator;
import com.infotech.isg.validation.ErrorCodes;

import org.springframework.stereotype.Component;

/**
 * Vopay validator for amount
 *
 * @author Sevak Gharibian
 */
@Component("VopayAmountValidator")
public class VopayAmountValidatorImpl implements AmountValidator {

    @Override
    public int validate(int amount, int action) {
        if (amount <= 0) {
            return ErrorCodes.INVALID_AMOUNT;
        }
        return ErrorCodes.OK;
    }
}
