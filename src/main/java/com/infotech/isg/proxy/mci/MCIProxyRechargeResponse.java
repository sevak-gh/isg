package com.infotech.isg.proxy.mci;

import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlElement;

/**
* representing MCI Recharge response object.
*
* @author Sevak Gharibian
*/
@XmlRootElement(name = "RechargeResult")
@XmlAccessorType(XmlAccessType.FIELD)
public class MCIProxyRechargeResponse {

    @XmlElement(name = "string")
    private List<String> response;

    public List<String> getResponse() {
        return response;
    }

    public void setResponse(List<String> response) {
        this.response = response;
    }

    public String getCode() {
        return response.get(0);
    }

    public String getDetail() {
        return response.get(1);
    }
}
