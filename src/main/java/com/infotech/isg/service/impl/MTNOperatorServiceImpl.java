package com.infotech.isg.service.impl;

import com.infotech.isg.service.OperatorService;
import com.infotech.isg.service.OperatorServiceResponse;

import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * implementation for MTN operator service
 *
 * @author Sevak Gharibian
 */
@Component("MTNOperatorService")
public class MTNOperatorServiceImpl implements OperatorService {

    private static final Logger LOG = LoggerFactory.getLogger(MTNOperatorServiceImpl.class);

    @Override
    public OperatorServiceResponse topup(String consumer, int amount, long transactionId) {
        return null;
    }
}
