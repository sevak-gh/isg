package com.infotech.isg.service.impl;

import com.infotech.isg.domain.Operator;
import com.infotech.isg.service.AccessControl;
import com.infotech.isg.repository.TransactionRepository;
import com.infotech.isg.validation.TransactionValidator;
import com.infotech.isg.validation.RequestValidator;
import com.infotech.isg.proxy.ServiceProvider;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
* MTN service implementation
*
* @author Sevak Gharibian
*/
@Component("MTNService")
public class MTNServiceImpl extends ISGServiceImpl {

    @Autowired
    public MTNServiceImpl(AccessControl accessControl,
                          TransactionRepository transactionRepository,
                          @Qualifier("MTNServiceProvider") ServiceProvider serviceProvider,
                          @Qualifier("MTNRequestValidator") RequestValidator requestValidator,
                          TransactionValidator transactionValidator) {
        this.accessControl = accessControl;
        this.transactionRepository = transactionRepository;
        this.serviceProvider = serviceProvider;
        this.requestValidator = requestValidator;
        this.transactionValidator = transactionValidator;
        this.operatorId = Operator.MTN_ID;
    }
}
