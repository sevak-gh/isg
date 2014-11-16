package com.infotech.isg.service;

/**
* representing service error codes.
*
* @author Sevak Garibian
*/
public class ErrorCodes {
    public static final int OK = 0;
    public static final int INTERNAL_SYSTEM_ERROR = -1;
    public static final int INSUFFICIENT_PARAMETERS = -2;
    public static final int INVALID_USERNAME_OR_PASSWORD = -3;
    public static final int DISABLED_CLIENT_ACCOUNT = -4;
    public static final int INVALID_CLIENT_IP = -5;
    public static final int FINANCIAL_TRANSACTION_ERROR = -6;
    public static final int FINANCIAL_TRANSACTION_VERIFICATION_FAILED = -7;
    public static final int INVALID_BANK_CODE = -8;
    public static final int INVALID_AMOUNT = -9;
    public static final int OPERATOR_SERVICE_ERROR = -10;
    public static final int BANK_SERVICE_UNAVAILABLE = -11;
    public static final int DOUBLE_SPENDING_TRANSACTION = -12;
    public static final int INVALID_BANK_RECEIPT = -13;
    public static final int AMOUNT_MISMATCHED = -14;
    public static final int REPETITIVE_TRANSACTION = -15;
    public static final int INVALID_OPERATOR_ACTION = -16;
    public static final int OPERATOR_SERVICE_UNAVAILABLE = -17;
    public static final int INVALID_PAYMENT_CHANNEL = -18;
    public static final int DISABLED_PAYMENT_CHANNEL = -19;
    public static final int TRANSACTION_REVERSE_ERROR = -20;
    public static final int STF_ERROR = -21;
    public static final int INVALID_CELL_NUMBER = -22;
    public static final int TRANSACTION_VERIFICATION_ERROR_DUE_TO_SOAP = -23;
    public static final int DISABLED_OPERATOR = -24;
    public static final int INVALID_OPERATOR = -25;
}
