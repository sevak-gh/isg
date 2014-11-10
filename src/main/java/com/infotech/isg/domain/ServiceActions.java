package com.infotech.isg.domain;

import java.util.List;
import java.util.Arrays;

/**
* representing supported service actions.
*
* @author Sevak Gharibian
*/
public class ServiceActions {


    public static final String TOPUP = "top-up";
    public static final String BULK = "bulk";
    public static final String PAY_BILL = "pay-bill";
    public static final String WOW = "wow";
    public static final String POST_WIMAX = "post_wimax";
    public static final String PRE_WIMAX = "pre_wimax";
    public static final String GPRS = "gprs";
    
    private static List<String> actions = Arrays.asList(TOPUP, BULK, PAY_BILL, WOW, POST_WIMAX, PRE_WIMAX, GPRS);

    public static boolean isActionExist(String action) {
        return actions.contains(action);
    }
}
