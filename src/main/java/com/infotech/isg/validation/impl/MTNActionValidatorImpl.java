package com.infotech.isg.validation.impl;

import com.infotech.isg.validation.ErrorCodes;
import com.infotech.isg.validation.ActionValidator;
import com.infotech.isg.domain.MTNServiceActions;

import org.springframework.stereotype.Component;

/**
* validator for MTN service actions
*
* @author Sevak Gharibian
*/
@Component("MTNActionValidator")
public class MTNActionValidatorImpl implements ActionValidator {

    @Override
    public int validate(String action) {
        return (MTNServiceActions.isActionExist(action)) ? ErrorCodes.OK : ErrorCodes.INVALID_OPERATOR_ACTION;
    }
}
