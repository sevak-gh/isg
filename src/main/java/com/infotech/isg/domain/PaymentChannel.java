package com.infotech.isg.domain;

/**
* domain object representing payment channel.
*
* @author Sevak Gharibian
*/
public class PaymentChannel {
    private String channel;
    private boolean isActive;

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }
}
