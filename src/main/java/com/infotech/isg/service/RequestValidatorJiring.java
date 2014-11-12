package com.infotech.isg.service;

import com.infotech.isg.domain.Operator;
import com.infotech.isg.repository.OperatorRepository;
import com.infotech.isg.repository.PaymentChannelRepository;
import com.infotech.isg.repository.ClientRepository;

/**
* validating Jiring service request.
*
* @author Sevak Gharibian
*/
public class RequestValidatorJiring extends RequestValidator {

    public RequestValidatorJiring(OperatorRepository operatorRepository, PaymentChannelRepository paymentChannelRepository, ClientRepository clientRepository) {
        this.operatorRepository = operatorRepository;
        this.paymentChannelRepository = paymentChannelRepository;
        this.clientRepository = clientRepository;
        this.operatorId = Operator.JIRING_ID;
    }
}

