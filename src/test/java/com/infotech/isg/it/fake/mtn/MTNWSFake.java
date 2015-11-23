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

    @Value("${mtn.infotech.url}")
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
        String channel = request.getParameterValue("ext_id");
        // check command
        if (items[0].equals("pay")) {
            if (items[1].equals("b")) {
                // pay-bill
                return mtnService.billPayment(items[2], Integer.parseInt(items[3]), Long.parseLong(trId.substring(4, trId.length())), channel);
            } else if (items[1].equals("d")) {
                // bulk transfer
                return mtnService.bulkTransfer(items[2], Integer.parseInt(items[3]), Long.parseLong(trId.substring(4, trId.length())), channel);
            } else {
                throw new UnsupportedOperationException("requested operation not supported");
            }
        } else {
            if (items.length > 3) {
                if (items[3].startsWith("19")) {
                    // wow
                    return mtnService.wow(items[0], Integer.parseInt(items[1]), Long.parseLong(trId.substring(4, trId.length())), channel);
                } else if (items[3].startsWith("43")) {
                    // gprs
                    return mtnService.gprs(items[0], Integer.parseInt(items[1]), Long.parseLong(trId.substring(4, trId.length())), channel);
               } else {
                    // gprs combo
                    String[] tokens = items[3].split("|");
                    return mtnService.gprsCombo(items[0], 
                                                Integer.parseInt(items[1]), 
                                                Long.parseLong(trId.substring(4, trId.length())), 
                                                Integer.parseInt(tokens[0]),
                                                channel);
                }
            } else {
                // topup, pre-wimax, post-wimax
                return mtnService.recharge(items[0], Integer.parseInt(items[1]), Long.parseLong(trId.substring(4, trId.length())), channel);
            }
        }
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
