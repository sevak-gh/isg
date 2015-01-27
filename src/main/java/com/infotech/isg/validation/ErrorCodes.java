package com.infotech.isg.validation;

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
    public static final int OPERATOR_SERVICE_ERROR_DONOT_REVERSE = -10;
    public static final int BANK_SERVICE_UNAVAILABLE = -11;
    public static final int DOUBLE_SPENDING_TRANSACTION = -12;
    public static final int INVALID_BANK_RECEIPT = -13;
    public static final int AMOUNT_MISMATCHED = -14;
    public static final int REPETITIVE_TRANSACTION = -15;
    public static final int INVALID_OPERATOR_ACTION = -16;
    public static final int OPERATOR_SERVICE_RESPONSE_NOK = -17;
    public static final int INVALID_PAYMENT_CHANNEL = -18;
    public static final int DISABLED_PAYMENT_CHANNEL = -19;
    public static final int TRANSACTION_REVERSE_ERROR = -20;
    public static final int STF_ERROR = -21;
    public static final int INVALID_CELL_NUMBER = -22;
    public static final int TRANSACTION_VERIFICATION_ERROR_DUE_TO_SOAP = -23;
    public static final int DISABLED_OPERATOR = -24;
    public static final int INVALID_OPERATOR = -25;
    public static final int TRANSACTION_ALREADY_FAILED = -26;
    public static final int OPERATOR_SERVICE_ERROR = -27;
    public static final int STF_RESOLVED_FAILED = -28;
    public static final int STF_RESOLVED_SUCCESSFUL = -29;

    public static String toString(int errorCode) {
        switch (errorCode) {
            case OK: return "OK";
            case INTERNAL_SYSTEM_ERROR: return "INTERNAL_SYSTEM_ERROR";
            case INSUFFICIENT_PARAMETERS: return "INSUFFICIENT_PARAMETERS";
            case INVALID_USERNAME_OR_PASSWORD: return "INVALID_USERNAME_OR_PASSWORD";
            case DISABLED_CLIENT_ACCOUNT: return "DISABLED_CLIENT_ACCOUNT";
            case INVALID_CLIENT_IP: return "INVALID_CLIENT_IP";
            case FINANCIAL_TRANSACTION_ERROR: return "FINANCIAL_TRANSACTION_ERROR";
            case FINANCIAL_TRANSACTION_VERIFICATION_FAILED: return "FINANCIAL_TRANSACTION_VERIFICATION_FAILED";
            case INVALID_BANK_CODE: return "INVALID_BANK_CODE";
            case INVALID_AMOUNT: return "INVALID_AMOUNT";
            case OPERATOR_SERVICE_ERROR_DONOT_REVERSE: return "OPERATOR_SERVICE_ERROR_DONOT_REVERSE";
            case BANK_SERVICE_UNAVAILABLE: return "BANK_SERVICE_UNAVAILABLE";
            case DOUBLE_SPENDING_TRANSACTION: return "DOUBLE_SPENDING_TRANSACTION";
            case INVALID_BANK_RECEIPT: return "INVALID_BANK_RECEIPT";
            case AMOUNT_MISMATCHED: return "AMOUNT_MISMATCHED";
            case REPETITIVE_TRANSACTION: return "REPETITIVE_TRANSACTION";
            case INVALID_OPERATOR_ACTION: return "INVALID_OPERATOR_ACTION";
            case OPERATOR_SERVICE_RESPONSE_NOK: return "OPERATOR_SERVICE_RESPONSE_NOK";
            case INVALID_PAYMENT_CHANNEL: return "INVALID_PAYMENT_CHANNEL";
            case DISABLED_PAYMENT_CHANNEL: return "DISABLED_PAYMENT_CHANNEL";
            case TRANSACTION_REVERSE_ERROR: return "TRANSACTION_REVERSE_ERROR";
            case STF_ERROR: return "STF_ERROR";
            case INVALID_CELL_NUMBER: return "INVALID_CELL_NUMBER";
            case TRANSACTION_VERIFICATION_ERROR_DUE_TO_SOAP: return "TRANSACTION_VERIFICATION_ERROR_DUE_TO_SOAP";
            case DISABLED_OPERATOR: return "DISABLED_OPERATOR";
            case INVALID_OPERATOR: return "INVALID_OPERATOR";
            case TRANSACTION_ALREADY_FAILED: return "TRANSACTION_ALREADY_FAILED";
            case OPERATOR_SERVICE_ERROR: return "OPERATOR_SERVICE_ERROR";
            case STF_RESOLVED_FAILED: return "STF_RESOLVED_FAILED";
            case STF_RESOLVED_SUCCESSFUL: return "STF_RESOLVED_SUCCESSFUL";
            default: return "";
        }
    }
}
