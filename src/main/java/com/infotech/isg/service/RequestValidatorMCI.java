package com.infotech.isg.service;

import com.infotech.isg.domain.Operator;
import com.infotech.isg.repository.OperatorRepository;
import com.infotech.isg.repository.PaymentChannelRepository;
import com.infotech.isg.repository.ClientRepository;
import com.infotech.isg.repository.TransactionRepository;

/**
* validating service request.
*
* @author Sevak Gharibian
*/
public class RequestValidatorMCI extends RequestValidator {

    public RequestValidatorMCI(OperatorRepository operatorRepository, PaymentChannelRepository paymentChannelRepository,
                               ClientRepository clientRepository, TransactionRepository transactionRepository) {
        this.operatorRepository = operatorRepository;
        this.paymentChannelRepository = paymentChannelRepository;
        this.clientRepository = clientRepository;
        this.transactionRepository = transactionRepository;
        this.operatorId = Operator.MCI_ID;
    }
}
