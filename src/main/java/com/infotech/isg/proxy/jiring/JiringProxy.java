package com.infotech.isg.proxy.jiring;

/**
 * proxy client for Jiring service.
 *
 * @author Sevak Gharibian
 */
public interface JiringProxy {
    public TCSResponse salesRequest(String consumer, int amount);
    public TCSResponse salesRequestExec(String token);
}
