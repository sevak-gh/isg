package com.infotech.isg.proxy;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlValue;

/**
* representing GetToken response object.
*
* @author SEvak Gahribian
*/
@XmlRootElement(name = "GetTokenResult")
@XmlAccessorType(XmlAccessType.FIELD)
public class MCIGetTokenResponse {
    @XmlValue
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
