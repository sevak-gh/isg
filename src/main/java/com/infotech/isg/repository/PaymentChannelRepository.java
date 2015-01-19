package com.infotech.isg.repository;

import com.infotech.isg.domain.PaymentChannel;

/**
 * repository for payent channel domain object.
 *
 * @author Sevak Gharibian
 */
public interface PaymentChannelRepository {
    public PaymentChannel findById(String id);
}
