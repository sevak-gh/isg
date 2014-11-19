package com.infotech.isg.proxy;

/**
* representing MCI service response.
*
* @author Sevak Gharibian
*/
public class MCIResponse {
    private String code;
    private String detail;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}
