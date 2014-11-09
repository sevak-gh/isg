package com.infotech.isg.domain;

/**
* domain object representing topup transaction.
*
* @author Sevak Gharibian
*/
public class Transaction {
    private long id;
    private int provider;
    private String token;
    private int type;
    private String state;
    private String resnum;
    private String refnum;
    private long revnum;
    private String clientIp;
    private long amount;
    private int channel;   
}
