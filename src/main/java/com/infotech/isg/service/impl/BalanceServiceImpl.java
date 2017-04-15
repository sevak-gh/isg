package com.infotech.isg.service.impl;

import com.infotech.isg.service.BalanceService;
import com.infotech.isg.domain.Balance;
import com.infotech.isg.repository.BalanceRepository;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * balance service impl
 *
 * @author Sevak Gharibian
 */
@Service
public class BalanceServiceImpl implements BalanceService {

    private final BalanceRepository balanceRepository;

    @Autowired
    public BalanceServiceImpl(BalanceRepository balanceRepository) {
        this.balanceRepository = balanceRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Balance findById(Integer id) {
        return balanceRepository.findById(id);
    }
}
