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
 * Vopay service implementation
 *
 * @author Sevak Gharibian
 */
@Service("VopayService")
public class VopayServiceImpl extends ISGServiceImpl {

    @Autowired
    public VopayServiceImpl(AccessControl accessControl,
                          TransactionRepository transactionRepository,
                          @Qualifier("VopayOperatorService") OperatorService operatorService,
                          @Qualifier("VopayRequestValidator") RequestValidator requestValidator,
                          TransactionValidator transactionValidator,
                          OperatorStatusRepository operatorStatusRepository) {
        this.accessControl = accessControl;
        this.transactionRepository = transactionRepository;
        this.operatorService = operatorService;
        this.requestValidator = requestValidator;
        this.transactionValidator = transactionValidator;
        this.operatorStatusRepository = operatorStatusRepository;
        this.operatorId = Operator.VOPAY_ID;
    }
}
