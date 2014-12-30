package com.infotech.isg.validation.impl;

import com.infotech.isg.validation.ErrorCodes;
import com.infotech.isg.validation.ActionValidator;
import com.infotech.isg.domain.JiringServiceActions;

import org.springframework.stereotype.Component;

/**
* validator for Jiring service actions
*
* @author Sevak Gharibian
*/
@Component("JiringActionValidator")
public class JiringActionValidatorImpl implements ActionValidator {

    @Override
    public int validate(String action) {
        return (JiringServiceActions.isActionExist(action)) ? ErrorCodes.OK : ErrorCodes.INVALID_OPERATOR_ACTION;
    }
}
