package com.infotech.isg.proxy;

/**
 * custom exception for Proxy access(connection/send/receive)
 *
 * @author Sevak Gharibian
 */
public class ProxyAccessException extends RuntimeException {

    public ProxyAccessException() {
    }

    public ProxyAccessException(String message) {
        super(message);
    }

    public ProxyAccessException(Throwable cause) {
        super(cause);
    }

    public ProxyAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
