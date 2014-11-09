package com.infotech.isg.domain;

import java.util.Map;
import java.util.HashMap;

/**
* domain object representing TeleCom operator.   
*
* @author Sevak Gharibian 
*/
public class Operator {
    public enum Status {
        ACTIVE,
        DISABLED
    };

    private int id;
    private String name;
    private Status status;
    private static Map<Integer, String> services = new HashMap<Integer, String>();

    static {
        services.put(1, "top-up");
        services.put(2, "bulk");
        services.put(3, "pay-bill");
        services.put(4, "wow");
        services.put(5, "post-wimax");
        services.put(6, "pre-wimax");
        services.put(7, "gprs");
    }

    public int getId() {
        return id;
    }

    public void setId() {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public static boolean isServiceExist(String service) {
        return services.containsValue(service);
    }
}

