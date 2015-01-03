package com.infotech.isg.validation.impl;

import com.infotech.isg.validation.ErrorCodes;
import com.infotech.isg.validation.ActionValidator;
import com.infotech.isg.domain.ServiceActions;

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
        if (!ServiceActions.isActionExist(action)) {
            return ErrorCodes.INVALID_OPERATOR_ACTION;
        }

        if ((ServiceActions.getActionCode(action) != ServiceActions.TOP_UP)
            && (ServiceActions.getActionCode(action) != ServiceActions.BULK)
            && (ServiceActions.getActionCode(action) != ServiceActions.PAY_BILL)
            && (ServiceActions.getActionCode(action) != ServiceActions.WOW)
            && (ServiceActions.getActionCode(action) != ServiceActions.POST_WIMAX)
            && (ServiceActions.getActionCode(action) != ServiceActions.PRE_WIMAX)
            && (ServiceActions.getActionCode(action) != ServiceActions.GPRS)) {
            return ErrorCodes.INVALID_OPERATOR_ACTION;
        }

        return ErrorCodes.OK;
    }
}
