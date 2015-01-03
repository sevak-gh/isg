package com.infotech.isg.proxy.mtn;

import com.infotech.isg.proxy.ServiceProvider;
import com.infotech.isg.proxy.ServiceProviderResponse;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
* implementation for MTN service provider.
*
* @author Sevak Gharibian
*/
@Component("MTNServiceProvider")
public class MTNServiceProviderImpl implements ServiceProvider {

    private static final Logger LOG = LoggerFactory.getLogger(MTNServiceProviderImpl.class);

    @Override
    public ServiceProviderResponse topup(String consumer, int amount, long transactionId) {
        return null;
    }
}
