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

    public static final int TOP_UP = 1;
    public static final int BULK = 2;
    public static final int PAY_BILL = 3;
    public static final int WOW = 4;
    public static final int POST_WIMAX = 5;
    public static final int PRE_WIMAX = 6;
    public static final int GPRS = 7;

    static {
        actions.put("top-up", TOP_UP);
        actions.put("bulk", BULK);
        actions.put("pay-bill", PAY_BILL);
        actions.put("wow", WOW);
        actions.put("post_wimax", POST_WIMAX);
        actions.put("pre_wimax", PRE_WIMAX);
        actions.put("gprs", GPRS);
    }

    public static boolean isActionExist(String action) {
        return actions.containsKey(action);
    }

    public static int getActionCode(String action) {
        return actions.get(action);
    }
}
