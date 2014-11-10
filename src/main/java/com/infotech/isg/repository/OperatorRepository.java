package com.infotech.isg.repository;

import com.infotech.isg.domain.Operator;

/**
* repository for Operator domain object.
*
* @author Sevak Gharibian
*/
public interface OperatorRepository {
    public Operator findById(int id);
}
