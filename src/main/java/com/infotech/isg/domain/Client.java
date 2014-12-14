package com.infotech.isg.domain;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

/**
* domain object representing ISG service client.
*
* @author Sevak Gharibian
*/
public class Client {
    private int id;
    private String username;
    private String password;
    private String name;
    private String contact;
    private String tel;
    private String vendor;
    private Date createdDate;
    private Date firstLoginDate;
    private Date lastLoginDate;
    private boolean isActive;
    private List<String> ips;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public List<String> getIps() {
        if (ips == null) {
            ips = new ArrayList<String>();
        }
        return ips;
    }

    public void addIp(String ip) {
        getIps().add(ip);
    }

    @Override
    public String toString() {
        return String.format("[%s:%s][%s]", username, (isActive) ? "Active" : "Disabled", getIps());
    }
}
