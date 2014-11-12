package com.infotech.isg.domain;

import java.util.Map;
import java.util.HashMap;

/**
* representing supported service actions.
*
* @author Sevak Gharibian
*/
public class ServiceActions {
    private static Map<String, Integer> actions = new HashMap<String, Integer>();

    static {
        actions.put("top-up", 1);
        actions.put("bulk", 2);
        actions.put("pay-bill", 3);
        actions.put("wow", 4);
        actions.put("post_wimax", 5);
        actions.put("pre_wimax", 6);
        actions.put("gprs", 7);
    }

    public static boolean isActionExist(String action) {
        return actions.containsKey(action);
    }

    public static int getActionCode(String action) {
        return actions.get(action); 
    }
}
