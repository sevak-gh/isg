package com.infotech.isg.proxy.jiring;

import com.infotech.isg.proxy.ServiceProvider;
import com.infotech.isg.proxy.ServiceProviderResponse;
import com.infotech.isg.service.ISGException;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
* implementation for Jiring service provider.
*
* @author Sevak Gharibian
*/
@Component("JiringServiceProvider")
public class JiringServiceProviderImpl implements ServiceProvider {

    private static final Logger LOG = LoggerFactory.getLogger(JiringServiceProviderImpl.class);

    @Override
    public ServiceProviderResponse topup(String consumer, int amount, long transactionId) {
        return null;
    }
}
