package com.infotech.isg.proxy;

/**
* generic service provider API
*
* null response means error in communication with service provider
* if result is ambiguous it will throw ISGException, should be set for STF
*
* @author Sevak Gharibian
*/
public interface ServiceProvider {
    public ServiceProviderResponse topup(String consumer, int amount, long transactionId);
}
