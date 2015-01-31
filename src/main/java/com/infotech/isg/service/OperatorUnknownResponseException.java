package com.infotech.isg.service;

/**
 * custom exception for operator service
 * used when operator response is ambiguous
 * client needs to verify the results later again (STF)
 *
 * @author Sevak Gharibian
 */
public class OperatorUnknownResponseException extends RuntimeException {

    public OperatorUnknownResponseException() {
    }

    public OperatorUnknownResponseException(String message) {
        super(message);
    }

    public OperatorUnknownResponseException(Throwable cause) {
        super(cause);
    }

    public OperatorUnknownResponseException(String message, Throwable cause) {
        super(message, cause);
    }
}
