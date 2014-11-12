package com.infotech.isg.repository;

import com.infotech.isg.domain.Transaction;

/**
* repository for Transaction domain object.
*
* @author Sevak Gharibian
*/
public interface TransactionRepository {
    public Transaction findByRefNumBankCodeClientId(String refNum, String bankCode, int clientId);
}
