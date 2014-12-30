package com.infotech.isg.domain;

import java.util.Map;
import java.util.HashMap;

/**
* representing supported MCI service actions.
*
* @author Sevak Gharibian
*/
public class MCIServiceActions {
    private static Map<String, Integer> actions = new HashMap<String, Integer>();

    public static final int TOP_UP = 1;

    static {
        actions.put("top-up", TOP_UP);
    }

    public static boolean isActionExist(String action) {
        return actions.containsKey(action);
    }

    public static int getActionCode(String action) {
        return actions.get(action);
    }
}
