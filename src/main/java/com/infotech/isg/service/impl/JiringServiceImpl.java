package com.infotech.isg.service.impl;

import com.infotech.isg.domain.Operator;
import com.infotech.isg.service.AccessControl;
import com.infotech.isg.repository.TransactionRepository;
import com.infotech.isg.repository.OperatorStatusRepository;
import com.infotech.isg.validation.TransactionValidator;
import com.infotech.isg.validation.RequestValidator;
import com.infotech.isg.service.OperatorService;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Jiring service implementation
 *
 * @author Sevak Gharibian
 */
@Service("JiringService")
public class JiringServiceImpl extends ISGServiceImpl {

    @Autowired
    public JiringServiceImpl(AccessControl accessControl,
                             TransactionRepository transactionRepository,
                             @Qualifier("JiringOperatorService") OperatorService operatorService,
                             @Qualifier("JiringRequestValidator") RequestValidator requestValidator,
                             TransactionValidator transactionValidator,
                             OperatorStatusRepository operatorStatusRepository) {
        this.accessControl = accessControl;
        this.transactionRepository = transactionRepository;
        this.operatorService = operatorService;
        this.requestValidator = requestValidator;
        this.transactionValidator = transactionValidator;
        this.operatorStatusRepository = operatorStatusRepository;
        this.operatorId = Operator.JIRING_ID;
    }
}
