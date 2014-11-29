package com.infotech.isg.validation;

import com.infotech.isg.repository.OperatorRepository;
import com.infotech.isg.repository.PaymentChannelRepository;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;


/**
* Jiring service request validator.
*
* @author Sevak Gharibian
*/
@Component("JiringValidator")
public class JiringRequestValidator extends RequestValidator {
    @Autowired
    public JiringRequestValidator(@Qualifier("JdbcOperatorRepository") OperatorRepository operatorRepository,
                                  @Qualifier("JdbcPaymentChannelRepository") PaymentChannelRepository paymentChannelRepository) {
        super(operatorRepository, paymentChannelRepository);
    }
}
