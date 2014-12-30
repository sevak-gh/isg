package com.infotech.isg.proxy;

/**
* service provider API
*
* null respone means error in communicating with service provider
* if result is ambiguous it will throw ISGException, should be set for STF
*
* @author Sevak Gharibian
*/
public interface ServiceProvider {
    public ServiceProviderResponse topup(String consumer, int amount, long transactionId);
}
