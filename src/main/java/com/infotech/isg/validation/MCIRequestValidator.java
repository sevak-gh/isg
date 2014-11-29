package com.infotech.isg.validation;

import com.infotech.isg.repository.OperatorRepository;
import com.infotech.isg.repository.PaymentChannelRepository;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;


/**
* MCI service request validator
*
* @author Sevak Gharibian
*/
@Component("MCIValidator")
public class MCIRequestValidator extends RequestValidator {
    @Autowired
    public MCIRequestValidator(@Qualifier("JdbcOperatorRepository") OperatorRepository operatorRepository,
                               @Qualifier("JdbcPaymentChannelRepository") PaymentChannelRepository paymentChannelRepository) {
        super(operatorRepository, paymentChannelRepository);
    }
}
