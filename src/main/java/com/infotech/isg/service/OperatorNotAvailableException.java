package com.infotech.isg.service;

/**
 * custom exception for operator service
 * used when operator connection/send/receive fails
 *
 * @author Sevak Gharibian
 */
public class OperatorNotAvailableException extends RuntimeException {

    public OperatorNotAvailableException() {
    }

    public OperatorNotAvailableException(String message) {
        super(message);
    }

    public OperatorNotAvailableException(Throwable cause) {
        super(cause);
    }

    public OperatorNotAvailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
