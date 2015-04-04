package com.infotech.isg.validation.impl;

import com.infotech.isg.validation.PaymentChannelValidator;
import com.infotech.isg.domain.PaymentChannel;
import com.infotech.isg.repository.PaymentChannelRepository;
import com.infotech.isg.validation.ErrorCodes;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

/**
 * validator for payment channels.
 *
 * @author Sevak Gharibian
 */
@Component("PaymentChannelValidator")
public class PaymentChannelValidatorImpl implements PaymentChannelValidator {

    private final PaymentChannelRepository paymentChannelRepository;

    @Autowired
    public PaymentChannelValidatorImpl(PaymentChannelRepository paymentChannelRepository) {
        this.paymentChannelRepository = paymentChannelRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public int validate(String channelId) {
        PaymentChannel channel = paymentChannelRepository.findById(channelId);
        if (channel == null) {
            return ErrorCodes.INVALID_PAYMENT_CHANNEL;
        }
        if (!channel.getIsActive()) {
            return ErrorCodes.DISABLED_PAYMENT_CHANNEL;
        }
        return ErrorCodes.OK;
    }
}
