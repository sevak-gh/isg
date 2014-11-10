package com.infotech.isg.service;

import com.infotech.isg.domain.Operator;
import com.infotech.isg.repository.OperatorRepository;
import com.infotech.isg.repository.PaymentChannelRepository;

/**
* validating service request.
*
* @author Sevak Gharibian
*/
public class RequestValidatorMCI extends RequestValidator {

    public RequestValidatorMCI(OperatorRepository operatorRepository, PaymentChannelRepository paymentChannelRepository) {
        this.operatorRepository = operatorRepository;
        this.paymentChannelRepository = paymentChannelRepository;            
        this.operatorId = Operator.MCI_ID;
    }
}
