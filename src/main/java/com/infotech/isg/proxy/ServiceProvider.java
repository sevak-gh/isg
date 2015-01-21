package com.infotech.isg.proxy;

/**
 * generic service provider API
 *
 * throws OperatorNotAvailableException if error in communication with service provider
 * throws OperatorUnknownResponseException if result is ambiguous, should be set for STF
 *
 * @author Sevak Gharibian
 */
public interface ServiceProvider {
    public ServiceProviderResponse topup(String consumer, int amount, long transactionId);
}
