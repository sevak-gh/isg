package com.infotech.isg.domain;

import java.util.Date;

/**
* domain object representing topup transaction.
*
* @author Sevak Gharibian
*/
public class Transaction {
    private long id;
    private int provider;
    private String token;
    private int action;     // type column 
    private String state;
    private String resNum;
    private String refNum;
    private long revNum;
    private String clientIp;
    private long amount;
    private int channel;
    private String consumer;
    private String bankCode;
    private int clientId;
    private String customerIp;
    private Date trDateTime;
    private int bankVerify;
    private Date verifyDateTime;
    private int status;
    private int operator;
    private String operatorCommand;
    private String operatorResponse;
    private String oepratorTId;         
    private Date operatorDateTime;
    private int stf;
    private int stfResult;
    private int opReverse;
    private int bkReverse;

    public long getId() {
        return id;
    }

    public int getProvider() {
        return provider;
    }

    public int getAction() {
        return action;
    }
    
    public String getRefNum() {
        return refNum;
    }

    public String getResNum() {
        return resNum;
    }
    
    public long getAmount() {
        return amount;
    }

    public int getChannel() {
        return channel;
    }

    public String getConsumer() {
        return consumer;
    }

    public String getCustomerIp() {
        return customerIp;
    }

    public int getStatus() {
        return status;
    }

    public int getOperator() {
        return operator;
    }

    public int getStf() {
        return stf;
    }
}
