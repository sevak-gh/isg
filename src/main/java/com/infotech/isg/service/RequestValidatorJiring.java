package com.infotech.isg.service;

import com.infotech.isg.domain.Operator;
import com.infotech.isg.repository.OperatorRepository;
import com.infotech.isg.repository.PaymentChannelRepository;
import com.infotech.isg.repository.ClientRepository;
import com.infotech.isg.repository.TransactionRepository;

/**
* validating Jiring service request.
*
* @author Sevak Gharibian
*/
public class RequestValidatorJiring extends RequestValidator {

    public RequestValidatorJiring(OperatorRepository operatorRepository, PaymentChannelRepository paymentChannelRepository, 
                                    ClientRepository clientRepository, TransactionRepository transactionRepository) {
        this.operatorRepository = operatorRepository;
        this.paymentChannelRepository = paymentChannelRepository;
        this.clientRepository = clientRepository;
        this.transactionRepository = transactionRepository;
        this.operatorId = Operator.JIRING_ID;
    }
}

