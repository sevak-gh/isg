package com.infotech.isg.validation.impl;

import com.infotech.isg.validation.BankCodeValidator;
import com.infotech.isg.domain.BankCodes;
import com.infotech.isg.validation.ErrorCodes;

import org.springframework.stereotype.Component;

/**
 * validator for bank codes
 *
 * @author Sevak Gharibian
 */
@Component("BankCodeValidator")
public class BankCodeValidatorImpl implements BankCodeValidator {

    @Override
    public int validate(String bankCode) {
        return (BankCodes.isCodeExist(bankCode)) ? ErrorCodes.OK : ErrorCodes.INVALID_BANK_CODE;
    }
}
