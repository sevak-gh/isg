package com.infotech.isg.service;

/**
* represents custom exception.
*
* @author Sevak Gharibian
*/
public class ISGException extends RuntimeException {
    private int code;

    public ISGException(int code, String message) {
        super(message);
        this.code = code;
    }

    public ISGException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public int getErrorCode() {
        return code;
    }
}
