package com.infotech.isg.service.impl;

import com.infotech.isg.service.AccessControl;
import com.infotech.isg.domain.Client;
import com.infotech.isg.validation.ErrorCodes;
import com.infotech.isg.util.HashGenerator;
import com.infotech.isg.repository.ClientRepository;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * performing access control logic for clients
 *
 * @author Sevak Gharibian
 */
@Service("AccessControl")
public class AccessControlImpl implements AccessControl {

    private final ClientRepository clientRepository;
    private Client client;
    private boolean isAuthenticated;

    @Autowired
    public AccessControlImpl(@Qualifier("JdbcClientRepository") ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public int authenticate(String username, String password, String remoteIp) {
        client = clientRepository.findByUsername(username);
        if (client == null) {
            return ErrorCodes.INVALID_USERNAME_OR_PASSWORD;
        }
        if (!client.getPassword().equals(HashGenerator.getSHA512(password))) {
            return ErrorCodes.INVALID_USERNAME_OR_PASSWORD;
        }
        if (!client.getIsActive()) {
            return ErrorCodes.DISABLED_CLIENT_ACCOUNT;
        }
        if (!client.getIps().contains(remoteIp)) {
            return ErrorCodes.INVALID_CLIENT_IP;
        }
        isAuthenticated = true;
        return ErrorCodes.OK;
    }

    @Override
    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    @Override
    public Client getClient() {
        return client;
    }
}

