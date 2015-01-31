package com.infotech.isg.service.impl;

import com.infotech.isg.domain.Operator;
import com.infotech.isg.service.AccessControl;
import com.infotech.isg.repository.TransactionRepository;
import com.infotech.isg.validation.TransactionValidator;
import com.infotech.isg.validation.RequestValidator;
import com.infotech.isg.service.ServiceProvider;

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
                          TransactionRepository transactionRepository,
                          @Qualifier("MCIServiceProvider") ServiceProvider serviceProvider,
                          @Qualifier("MCIRequestValidator") RequestValidator requestValidator,
                          TransactionValidator transactionValidator) {
        this.accessControl = accessControl;
        this.transactionRepository = transactionRepository;
        this.serviceProvider = serviceProvider;
        this.requestValidator = requestValidator;
        this.transactionValidator = transactionValidator;
        this.operatorId = Operator.MCI_ID;
    }
}
