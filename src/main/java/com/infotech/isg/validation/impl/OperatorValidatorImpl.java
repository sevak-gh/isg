package com.infotech.isg.validation.impl;

import com.infotech.isg.validation.OperatorValidator;
import com.infotech.isg.domain.Operator;
import com.infotech.isg.repository.OperatorRepository;
import com.infotech.isg.validation.ErrorCodes;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
* validator for service provider opertor.
*
* @author Sevak Gharibian
*/
@Component("OperatorValidator")
public class OperatorValidatorImpl implements OperatorValidator {

    private final OperatorRepository operatorRepository;

    @Autowired
    public OperatorValidatorImpl(@Qualifier("JdbcOperatorRepository") OperatorRepository operatorRepository) {
        this.operatorRepository = operatorRepository;
    }

    @Override
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
