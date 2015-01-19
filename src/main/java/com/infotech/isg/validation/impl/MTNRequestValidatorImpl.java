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
 * MTN request validator.
 *
 * @author Sevak Gharibian
 */
@Component("MTNRequestValidator")
public class MTNRequestValidatorImpl extends RequestValidatorImpl {

    @Autowired
    public MTNRequestValidatorImpl(@Qualifier("MTNAmountValidator") AmountValidator amountValidator,
                                   @Qualifier("MTNCellNumberValidator") CellNumberValidator cellNumberValidator,
                                   @Qualifier("MTNActionValidator") ActionValidator actionValidator,
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
