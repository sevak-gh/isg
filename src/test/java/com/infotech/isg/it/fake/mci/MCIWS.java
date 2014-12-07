package com.infotech.isg.it.fake.mci;

import com.infotech.isg.proxy.mci.MCIProxyRechargeResponse;
import com.infotech.isg.proxy.mci.MCIProxyGetTokenResponse;

import java.util.List;
import java.util.ArrayList;
import javax.jws.WebService;
import javax.jws.HandlerChain;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;
import javax.xml.ws.Endpoint;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

@WebService(name = "MCIWS", targetNamespace = "http://mci.service/")
@HandlerChain(file = "handler-chain.xml")
@SOAPBinding(style = Style.DOCUMENT, use = Use.LITERAL)
@Component
public class MCIWS {

    private MCIService mciService;
    private Endpoint ep;

    @Value("${mci.url}")
    private String url;

    @WebMethod(exclude = true)
    public void setServiceImpl(MCIService mciService) {
        this.mciService = mciService;
    }

    @WebMethod(operationName = "GetToken", action = "http://mci.service/GetToken")
    @WebResult(name = "GetTokenResult")
    public MCIProxyGetTokenResponse getToken() {
        return mciService.getToken();
    }

    @WebMethod(operationName = "Recharge", action = "http://mci.service/Recharge")
    @WebResult(name = "RechargeResult")
    public MCIProxyRechargeResponse recharge(@WebParam(name = "BrokerID") String token,
            @WebParam(name = "MobileNumber") String consumer,
            @WebParam(name = "CardAmount") int amount,
            @WebParam(name = "TransactionID") long trId) {
        return mciService.recharge(token, consumer, amount, trId);
    }

    @WebMethod(exclude = true)
    public void publish() {
        if ((ep != null) && (ep.isPublished())) {
            throw new RuntimeException("EP already published");
        }

        ep = Endpoint.publish(url, this);
    }

    @WebMethod(exclude = true)
    public void stop() {
        if (ep != null) {
            ep.stop();
        }
    }
}
