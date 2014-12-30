package com.infotech.isg.validation.impl;

import com.infotech.isg.validation.ErrorCodes;
import com.infotech.isg.validation.ActionValidator;
import com.infotech.isg.domain.ServiceActions;

import org.springframework.stereotype.Component;

/**
* validator for service actions
*
* @author Sevak Gharibian
*/
@Component("ActionValidator")
public class ActionValidatorImpl implements ActionValidator {

    @Override
    public int validate(String action) {
        return (ServiceActions.isActionExist(action)) ? ErrorCodes.OK : ErrorCodes.INVALID_OPERATOR_ACTION;
    }
}
