package com.infotech.isg.validation.impl;

import com.infotech.isg.validation.AmountValidator;
import com.infotech.isg.validation.CellNumberValidator;
import com.infotech.isg.validation.ActionValidator;
import com.infotech.isg.validation.BankCodeValidator;
import com.infotech.isg.validation.OperatorValidator;
import com.infotech.isg.validation.PaymentChannelValidator;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
* MCI request validator.
*
* @author Sevak Gharibian
*/
@Component("MCIRequestValidator")
public class MCIRequestValidatorImpl extends RequestValidatorImpl {

    @Autowired
    public MCIRequestValidatorImpl(@Qualifier("MCIAmountValidator") AmountValidator amountValidator,
                                   @Qualifier("MCICellNumberValidator") CellNumberValidator cellNumberValidator,
                                   @Qualifier("MCIActionValidator") ActionValidator actionValidator,
                                   BankCodeValidator bankCodeValidator,
                                   OperatorValidator operatorValidator,
                                   PaymentChannelValidator paymentChannelValidator) {
        this.amountValidator = amountValidator;
        this.cellNumberValidator = cellNumberValidator;
        this.actionValidator = actionValidator;
        this.bankCodeValidator = bankCodeValidator;
        this.operatorValidator = operatorValidator;
        this.paymentChannelValidator = paymentChannelValidator;
    }
}
