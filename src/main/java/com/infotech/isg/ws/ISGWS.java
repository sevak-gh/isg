package com.infotech.isg.ws;

import com.infotech.isg.service.ISGService;
import com.infotech.isg.service.ISGServiceResponse;
import com.infotech.isg.validation.ErrorCodes;

import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.annotation.Resource;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
import javax.servlet.http.HttpServletRequest;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;
import javax.jws.soap.SOAPBinding.ParameterStyle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
* publishes ISG service endpoint through XML Web service.
*
* @author Sevak Gharibian
*/
@WebService(name = "ISGWS", targetNamespace = "urn:TopUpWSDL")
@SOAPBinding(style = Style.DOCUMENT, use = Use.LITERAL)
@Service("ISGWS")
public class ISGWS {

    private static final Logger LOG = LoggerFactory.getLogger(ISGWS.class);

    @Resource
    private WebServiceContext context;

    private final ISGService mtnService;
    private final ISGService mciService;
    private final ISGService jiringService;

    /**
    * gets client remote IP through web service context
    */
    private String getClientIp() {
        MessageContext mc = context.getMessageContext();
        HttpServletRequest request = (HttpServletRequest)mc.get(MessageContext.SERVLET_REQUEST);
        return request.getRemoteAddr();
    }

    @Autowired
    public ISGWS(@Qualifier("MTNService") ISGService mtnService,
                 @Qualifier("MCIService") ISGService mciService,
                 @Qualifier("JiringService") ISGService jiringService) {
        this.mtnService = mtnService;
        this.mciService = mciService;
        this.jiringService = jiringService;
    }

    /**
    * represents MCI service.
    *
    */
    @WebMethod(operationName = "MCI", action = "urn:TopUpWSDL/MCI")
    @WebResult(name = "MCIResponse")
    public ISGServiceResponse mci(@WebParam(name = "username") String username,
                                  @WebParam(name = "password") String password,
                                  @WebParam(name = "bankcode") String bankCode,
                                  @WebParam(name = "amount") int amount,
                                  @WebParam(name = "channel") int channel,
                                  @WebParam(name = "state") String state,
                                  @WebParam(name = "bankreceipt") String bankReceipt,
                                  @WebParam(name = "orderid") String orderId,
                                  @WebParam(name = "consumer") String consumer,
                                  @WebParam(name = "customerip") String customerIp) {

        ISGServiceResponse response = mciService.topup(username, password, bankCode, amount, channel,
                                      state, bankReceipt, orderId, consumer, customerIp,
                                      getClientIp(), "top-up");

        return response;
    }
}
