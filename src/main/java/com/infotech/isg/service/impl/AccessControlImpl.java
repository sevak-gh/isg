package com.infotech.isg.service.impl;

import com.infotech.isg.service.AccessControl;
import com.infotech.isg.domain.Client;
import com.infotech.isg.validation.ErrorCodes;
import com.infotech.isg.util.HashGenerator;
import com.infotech.isg.repository.ClientRepository;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * performing access control logic for clients
 *
 * @author Sevak Gharibian
 */
@Service
public class AccessControlImpl implements AccessControl {

    private final ClientRepository clientRepository;

    @Autowired
    public AccessControlImpl(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public int authenticate(String username, String password, String remoteIp) {
        Client client = clientRepository.findByUsername(username);
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
        return ErrorCodes.OK;
    }

    @Override
    @Transactional(readOnly = true)
    public Client getClient(String username) {
        Client client = clientRepository.findByUsername(username);
        return client;
    }
}

