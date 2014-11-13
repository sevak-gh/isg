package com.infotech.isg.domain;

/**
* domain object representing payment channel.
*
* @author Sevak Gharibian
*/
public class PaymentChannel {
    private String id;
    private boolean isActive;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }
}
