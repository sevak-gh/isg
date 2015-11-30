package com.infotech.isg.validation.impl;

import com.infotech.isg.validation.ErrorCodes;
import com.infotech.isg.validation.ActionValidator;
import com.infotech.isg.domain.ServiceActions;

import org.springframework.stereotype.Component;

/**
 * validator for Vopay service actions
 *
 * @author Sevak Gharibian
 */
@Component("VopayActionValidator")
public class VopayActionValidatorImpl implements ActionValidator {

    @Override
    public int validate(String action) {

        // action is mapped to vopay package name
        // which is dynamic, so for now just check
        // not to be null or empty
        
        if ((action == null) || (action.isEmpty())) {
            return ErrorCodes.INVALID_OPERATOR_ACTION;
        }

        return ErrorCodes.OK;
    }
}
