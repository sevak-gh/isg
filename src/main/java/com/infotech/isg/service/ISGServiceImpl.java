package com.infotech.isg.service;

import org.springframework.stereotype.Service;

/**
* ISG service implementation.
*
* @author Sevak Gharibian
*/
@Service("ISGService")
public class ISGServiceImpl implements ISGService {
    @Override
    public ISGServiceResponse mci(String username, String password,
                                  String bankCode, int amount,
                                  int channel, String state,
                                  String bankReceipt, String orderId,
                                  String consumer, String customerIp) {
        //TODO to be completed
        return new ISGServiceResponse() {
            {
                setStatus("OK");
                setISGDoc(567);
                setOPRDoc("YES");
            }
        };
    }
}
