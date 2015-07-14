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
        // no amount validation yet
        return ErrorCodes.OK;
    }
}
