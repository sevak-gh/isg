package com.infotech.isg.service;

/**
 * generic operator service response.
 *
 * @author Sevak Gharibian
 */
public class OperatorServiceResponse {

    private String code;
    private String message;
    private String transactionId;
    private String status;
    private String token;
    private String param1;
    private String param2;
    private String param3;
    private String param4;

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

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public String getParam1() {
        return param1;
    }

    public void setParam1(String param1) {
        this.param1 = param1;
    }

    public String getParam2() {
        return param2;
    }

    public void setParam2(String param2) {
        this.param2 = param2;
    }

    public String getParam3() {
        return param3;
    }

    public void setParam3(String param3) {
        this.param3 = param3;
    }

    public String getParam4() {
        return param4;
    }

    public void setParam4(String param4) {
        this.param4 = param4;
    }

    @Override
    public String toString() {
        return String.format("OperatorResponse[code:%s, message:%s, trId:%s, status:%s, params:%s,%s,%s,%s]",
                             code, message, transactionId, status, param1, param2, param3, param4);
    }
}
