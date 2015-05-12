package com.infotech.isg.service.impl;

import com.infotech.isg.service.OperatorService;
import com.infotech.isg.service.OperatorServiceResponse;
import com.infotech.isg.service.OperatorNotAvailableException;
import com.infotech.isg.service.OperatorUnknownResponseException;
import com.infotech.isg.proxy.ProxyAccessException;
import com.infotech.isg.proxy.mtn.MTNProxy;
import com.infotech.isg.proxy.mtn.MTNProxyImpl;
import com.infotech.isg.proxy.mtn.MTNProxyResponse;
import com.infotech.isg.domain.ServiceActions;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * implementation for MTN operator service
 *
 * @author Sevak Gharibian
 */
@Service("MTNOperatorService")
public class MTNOperatorServiceImpl implements OperatorService {

    private static final Logger LOG = LoggerFactory.getLogger(MTNOperatorServiceImpl.class);

    @Value("${mtn.url}")
    private String url;

    @Value("${mtn.username}")
    private String username;

    @Value("${mtn.password}")
    private String password;

    @Value("${mtn.namespace}")
    private String namespace;

    @Value("${mtn.vendor}")
    private String vendor;

    @Override
    public OperatorServiceResponse topup(String consumer, int amount, long transactionId, String action) {
        MTNProxy mtnProxy = new MTNProxyImpl(url, username, password, vendor, namespace);

        MTNProxyResponse mtnResponse = null;
        try {
            switch (ServiceActions.getActionCode(action)) {
                case ServiceActions.TOP_UP:
                    mtnResponse = mtnProxy.recharge(consumer, amount, transactionId);
                    break;

                case ServiceActions.BULK:
                    mtnResponse = mtnProxy.bulkTransfer(consumer, amount, transactionId);
                    break;

                case ServiceActions.PAY_BILL:
                    mtnResponse = mtnProxy.billPayment(consumer, amount, transactionId);
                    break;

                case ServiceActions.WOW:
                    mtnResponse = mtnProxy.wow(consumer, amount, transactionId);
                    break;

                case ServiceActions.POST_WIMAX:
                    mtnResponse = mtnProxy.postPaidWimax(consumer, amount, transactionId);
                    break;

                case ServiceActions.PRE_WIMAX:
                    mtnResponse = mtnProxy.prePaidWimax(consumer, amount, transactionId);
                    break;

                case ServiceActions.GPRS:
                    mtnResponse = mtnProxy.gprs(consumer, amount, transactionId);
                    break;

                case ServiceActions.GPRS_DAILY:
                    mtnResponse = mtnProxy.gprsDaily(consumer, amount, transactionId);
                    break;

                case ServiceActions.GPRS_WEEKLY:
                    mtnResponse = mtnProxy.gprsWeekly(consumer, amount, transactionId);
                    break;

                case ServiceActions.GPRS_MONTHLY:
                    mtnResponse = mtnProxy.gprsMonthly(consumer, amount, transactionId);
                    break;

                default: break;
            }
        } catch (ProxyAccessException e) {
            throw new OperatorUnknownResponseException("response unknown/ambiguous from MTN", e);
        }

        // check recharge response
        if ((mtnResponse == null)
            || (mtnResponse.getTransactionId() == null)
            || (mtnResponse.getOrigResponseMessage() == null)
            || (mtnResponse.getCommandStatus() == null)
            || (mtnResponse.getResultCode() == null)) {
            // invalid response, should be set for STF
            throw new OperatorUnknownResponseException("response invalid from MTN");
        }

        // set response, no token for MTN
        OperatorServiceResponse response = new OperatorServiceResponse();
        response.setCode(mtnResponse.getResultCode());
        response.setMessage(mtnResponse.getOrigResponseMessage());
        response.setTransactionId(mtnResponse.getTransactionId());
        response.setStatus(mtnResponse.getCommandStatus());

        return response;
    }

    @Override
    public OperatorServiceResponse getBill(String consumer) {
        throw new UnsupportedOperationException("get bill amount not defined in MTN");
    }
}
