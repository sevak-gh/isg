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

    public void setStatus(String status) {
        this.status = status;
    }

    public long getISGDoc() {
        return isgDoc;
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

    @Override
    public String toString() {
        return String.format("[status:%s,ISGDoc:%d,oprDoc:%s]", status, isgDoc, oprDoc);
    }
}
