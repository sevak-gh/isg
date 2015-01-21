package com.infotech.isg.proxy.jiring;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;


/**
 * representing TCS request for jiring.
 *
 * @author Sevak Gharibian
 */
@XmlRootElement(name = "TCSRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class TCSRequest {

    private static class Function {
        @XmlAttribute(name = "name")
        private String name;

        @XmlElement(name = "param1")
        private String param1;

        @XmlElement(name = "param2")
        private String param2;

        @XmlElement(name = "param4")
        private String param4;

        @XmlElement(name = "param6")
        private String param6;

        @XmlElement(name = "param7")
        private String param7;
    }

    @XmlElement(name = "UserName")
    private String username;

    @XmlElement(name = "Password")
    private String password;

    @XmlElement(name = "Function")
    private Function function;

    public TCSRequest() {
        this.function = new Function();
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setFunctionName(String name) {
        this.function.name = name;
    }

    public void setFunctionParam1(String param1) {
        this.function.param1 = param1;
    }

    public void setFunctionParam2(String param2) {
        this.function.param2 = param2;
    }

    public void setFunctionParam4(String param4) {
        this.function.param4 = param4;
    }

    public void setFunctionParam6(String param6) {
        this.function.param6 = param6;
    }

    public void setFunctionParam7(String param7) {
        this.function.param7 = param7;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getFunctionName() {
        return function.name;
    }

    public String getFunctionParam1() {
        return function.param1;
    }

    public String getFunctionParam2() {
        return function.param2;
    }

    public String getFunctionParam4() {
        return function.param4;
    }

    public String getFunctionParam6() {
        return function.param6;
    }

    public String getFunctionParam7() {
        return function.param7;
    }

    @Override
    public String toString() {
        return String.format("TCSRequest[username:%s, password:%s, function => %s(%s,%s,%s,%s,%s)]",
                             username, password, function.name,
                             function.param1, function.param2,
                             function.param4, function.param6,
                             function.param7);
    }
}

