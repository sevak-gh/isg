package com.infotech.isg.service;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;

/**
 * represents ISG service response object.
 *
 * @author Sevak Gharibian
 */
@XmlRootElement(name = "ISGResponse", namespace = "urn:TopUpWSDL")
@XmlType(name = "ISGResponse", namespace = "urn:TopUpWSDL")
@XmlAccessorType(XmlAccessType.FIELD)
public class ISGServiceResponse {

    @XmlElement(name = "Status")
    private String status;

    @XmlElement(name = "ISGDoc")
    private long isgDoc;

    @XmlElement(name = "oprDoc")
    private String oprDoc;

    @XmlElement(name = "message")
    private String message;

    @XmlElement(name = "param1")
    private String param1;

    @XmlElement(name = "param2")
    private String param2;

    @XmlElement(name = "param3")
    private String param3;

    @XmlElement(name = "param4")
    private String param4;

    public String getStatus() {
        return status;
    }

    public ISGServiceResponse() {
    }

    public ISGServiceResponse(String status, long isgDoc, String oprDoc) {
        this.status = status;
        this.isgDoc = isgDoc;
        this.oprDoc = oprDoc;
    }

    public ISGServiceResponse(String status, long isgDoc, String oprDoc, String message,
                              String param1, String param2, String param3, String param4) {
        this.status = status;
        this.isgDoc = isgDoc;
        this.oprDoc = oprDoc;
        this.message = message;
        this.param1 = param1;
        this.param2 = param2;
        this.param3 = param3;
        this.param4 = param4;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getISGDoc() {
        return isgDoc;
    }

    public String getMessage() {
        return message;
    }

    public String getParam1() {
        return param1;
    }

    public String getParam2() {
        return param2;
    }

    public String getParam3() {
        return param3;
    }

    public String getParam4() {
        return param4;
    }

    public void setISGDoc(long isgDoc) {
        this.isgDoc = isgDoc;
    }

    public String getOPRDoc() {
        return oprDoc;
    }

    public void setOPRDoc(String oprDoc) {
        this.oprDoc = oprDoc;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setParam1(String param1) {
        this.param1 = param1;
    }

    public void setParam2(String param2) {
        this.param2 = param2;
    }

    public void setParam3(String param3) {
        this.param3 = param3;
    }

    public void setParam4(String param4) {
        this.param4 = param4;
    }

    @Override
    public String toString() {
        return String.format("[status:%s,ISGDoc:%d,oprDoc:%s,message:%s]", status, isgDoc, oprDoc, message);
    }
}
