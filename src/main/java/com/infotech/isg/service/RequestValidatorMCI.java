package com.infotech.isg.service;

import com.infotech.isg.domain.Operator;
import com.infotech.isg.repository.OperatorRepository;
import com.infotech.isg.repository.PaymentChannelRepository;
import com.infotech.isg.repository.ClientRepository;

/**
* validating service request.
*
* @author Sevak Gharibian
*/
public class RequestValidatorMCI extends RequestValidator {

    public RequestValidatorMCI(OperatorRepository operatorRepository, PaymentChannelRepository paymentChannelRepository, ClientRepository clientRepository) {
        this.operatorRepository = operatorRepository;
        this.paymentChannelRepository = paymentChannelRepository;
        this.clientRepository = clientRepository;
        this.operatorId = Operator.MCI_ID;
    }
}
