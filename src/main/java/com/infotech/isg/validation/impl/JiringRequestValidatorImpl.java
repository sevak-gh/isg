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
 * Jiring request validator.
 *
 * @author Sevak Gharibian
 */
@Component("JiringRequestValidator")
public class JiringRequestValidatorImpl extends RequestValidatorImpl {

    @Autowired
    public JiringRequestValidatorImpl(@Qualifier("MCIAmountValidator") AmountValidator amountValidator,
                                      @Qualifier("GenericCellNumberValidator") CellNumberValidator cellNumberValidator,
                                      @Qualifier("JiringActionValidator") ActionValidator actionValidator,
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
