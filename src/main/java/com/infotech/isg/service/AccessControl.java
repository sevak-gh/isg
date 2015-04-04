package com.infotech.isg.service;

import com.infotech.isg.domain.Client;

/**
 * providing access control service for clients
 *
 * @author Sevak Gharibian
 */
public interface AccessControl {
    public int authenticate(String username, String password, String remoteIp);
    public Client getClient(String username);
}
