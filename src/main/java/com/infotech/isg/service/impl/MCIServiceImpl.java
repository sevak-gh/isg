package com.infotech.isg.service.impl;

import com.infotech.isg.domain.Operator;
import com.infotech.isg.service.AccessControl;
import com.infotech.isg.repository.TransactionRepository;
import com.infotech.isg.repository.OperatorStatusRepository;
import com.infotech.isg.validation.TransactionValidator;
import com.infotech.isg.validation.RequestValidator;
import com.infotech.isg.service.OperatorService;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * MCI service implementation
 *
 * @author Sevak Gharibian
 */
@Component("MCIService")
public class MCIServiceImpl extends ISGServiceImpl {

    @Autowired
    public MCIServiceImpl(AccessControl accessControl,
                          @Qualifier("JdbcTransactionRepository") TransactionRepository transactionRepository,
                          @Qualifier("MCIOperatorService") OperatorService operatorService,
                          @Qualifier("MCIRequestValidator") RequestValidator requestValidator,
                          TransactionValidator transactionValidator,
                          @Qualifier("JdbcOperatorStatusRepository") OperatorStatusRepository operatorStatusRepository) {
        this.accessControl = accessControl;
        this.transactionRepository = transactionRepository;
        this.operatorService = operatorService;
        this.requestValidator = requestValidator;
        this.transactionValidator = transactionValidator;
        this.operatorStatusRepository = operatorStatusRepository;
        this.operatorId = Operator.MCI_ID;
    }
}
