package com.infotech.isg.it.fake.mtn;

import com.infotech.isg.proxy.mtn.MTNProxy;
import com.infotech.isg.proxy.mtn.MTNProxyResponse;
import com.infotech.isg.proxy.mtn.MTNProxyRequest;

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

/**
 * fake web service for MTN, used for integration tests
 * annotated as spring component so that app properties can be used
 *
 * @author Sevak Gharibian
 */
@WebService(name = "MTNWSFake", targetNamespace = "http://erefill.nokia.com")
@SOAPBinding(style = Style.DOCUMENT, use = Use.LITERAL)
@Component
public class MTNWSFake {
    private MTNProxy mtnService;
    private Endpoint ep;

    @Value("${mtn.url}")
    private String url;

    @WebMethod(exclude = true)
    public void setServiceImpl(MTNProxy mtnService) {
        this.mtnService = mtnService;
    }

    @WebMethod(operationName = "processRequest")
    @WebResult(name = "processRequestResponse")
    public MTNProxyResponse topup(@WebParam(name = "ETIRequest") MTNProxyRequest request) {
        String command = request.getCommand();
        String[] items = command.split(":");
        String trId = request.getParameterValue("ext_tid");
        return mtnService.recharge(items[0], Integer.parseInt(items[1]), Long.parseLong(trId.substring(4, trId.length())));
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
