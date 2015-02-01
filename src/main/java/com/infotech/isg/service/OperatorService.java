package com.infotech.isg.service;

/**
 * generic operator service API
 *
 * throws OperatorNotAvailableException if error in communication with service provider
 * throws OperatorUnknownResponseException if result is ambiguous, should be set for STF
 *
 * @author Sevak Gharibian
 */
public interface OperatorService {
    public OperatorServiceResponse topup(String consumer, int amount, long transactionId);
}
