package com.infotech.isg.domain;

import java.util.Map;
import java.util.Set;
import java.util.HashMap;

/**
* domain object containing bank codes.
*
* @author Sevak Gharibian
*/
public class BankCodes {
    
    private static Map<String, String> codes = new HashMap<String, String>();
    
    static {
        codes.put("056", "SAMAN");
        codes.put("055", "EN");
        codes.put("054", "PARSIAN");
        codes.put("057", "PASARGAD");
        codes.put("021", "POSTBANK");
        codes.put("018", "TEJARAT");
        codes.put("051", "TOSEE");
        codes.put("020", "TOSEE_SADERAT");
        codes.put("013", "REFAH");
        codes.put("015", "SEPAH");
        codes.put("058", "SARMAYEH");
        codes.put("019", "SADERAT");
        codes.put("011", "SANAT_MADAN");
        codes.put("053", "KARAFARIN");
        codes.put("016", "KESHAVARZI");
        codes.put("014", "MASKAN");
        codes.put("012", "MELLAT");
        codes.put("017", "BMI");
    }

    public static boolean isCodeExist(String code) {
        return codes.containsKey(code);
    }
    
    public static Set<String> getCodes() {
        return codes.keySet();
    }
}
