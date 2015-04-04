package com.infotech.isg.validation.impl;

import com.infotech.isg.validation.OperatorValidator;
import com.infotech.isg.domain.Operator;
import com.infotech.isg.repository.OperatorRepository;
import com.infotech.isg.validation.ErrorCodes;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

/**
 * validator for service provider opertor.
 *
 * @author Sevak Gharibian
 */
@Component("OperatorValidator")
public class OperatorValidatorImpl implements OperatorValidator {

    private final OperatorRepository operatorRepository;

    @Autowired
    public OperatorValidatorImpl(OperatorRepository operatorRepository) {
        this.operatorRepository = operatorRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public int validate(int operatorId) {
        Operator operator = operatorRepository.findById(operatorId);
        if (operator == null) {
            return ErrorCodes.INVALID_OPERATOR;
        }
        if (!operator.getIsActive()) {
            return ErrorCodes.DISABLED_OPERATOR;
        }
        return ErrorCodes.OK;
    }
}
