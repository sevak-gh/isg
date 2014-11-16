package com.infotech.isg.domain;

import java.util.Map;
import java.util.HashMap;

/**
* domain object representing Telecom operator.
*
* @author Sevak Gharibian
*/
public class Operator {

    private int id;
    private String name;
    private boolean isActive;

    public static final int MTN_ID = 1;
    public static final int MCI_ID = 2;
    public static final int JIRING_ID = 3;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }
}

