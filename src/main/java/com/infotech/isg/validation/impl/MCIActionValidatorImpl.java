package com.infotech.isg.validation.impl;

import com.infotech.isg.validation.ErrorCodes;
import com.infotech.isg.validation.ActionValidator;
import com.infotech.isg.domain.MCIServiceActions;

import org.springframework.stereotype.Component;

/**
* validator for MCI service actions
*
* @author Sevak Gharibian
*/
@Component("MCIActionValidator")
public class MCIActionValidatorImpl implements ActionValidator {

    @Override
    public int validate(String action) {
        return (MCIServiceActions.isActionExist(action)) ? ErrorCodes.OK : ErrorCodes.INVALID_OPERATOR_ACTION;
    }
}
