package com.infotech.isg.proxy;

/**
* service provider response.
*
* @author Sevak Gharibian
*/
public class ServiceProviderResponse {

    private String code;
    private String message;
    private String transactionId;
    private String status;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return String.format("[code:%s, message:%s, trId:%s, status:%s]", code, message, transactionId, status);
    }
}