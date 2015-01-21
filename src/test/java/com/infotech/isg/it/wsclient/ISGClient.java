package com.infotech.isg.it.wsclient;

import com.infotech.isg.service.ISGServiceResponse;
import com.infotech.isg.proxy.SOAPHelper;

import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPException;
import javax.xml.namespace.QName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

/**
 * web service client for ISG service
 * used for integration test only.
 *
 * @author Sevak Gharibian
 */
@Component
public class ISGClient {

    private static final Logger LOG = LoggerFactory.getLogger(ISGClient.class);

    @Value("${isg.url}")
    private String url;

    @Value("${isg.namespace}")
    private String namespace;

    public ISGServiceResponse mci(String username, String password,
                                  String bankCode, int amount,
                                  int channel, String state,
                                  String bankReceipt, String orderId,
                                  String consumer, String customerIp) {
        // create empty soap request
        SOAPMessage request = SOAPHelper.createSOAPRequest(namespace, namespace + "/MCI");

        // add request body/header
        try {
            SOAPBody body = request.getSOAPBody();
            SOAPBodyElement bodyElement = body.addBodyElement(new QName(namespace, "MCI", "ns"));
            SOAPElement element = bodyElement.addChildElement(new QName("username"));
            element.addTextNode(username);
            element = bodyElement.addChildElement(new QName("password"));
            element.addTextNode(password);
            element = bodyElement.addChildElement(new QName("bankcode"));
            element.addTextNode(bankCode);
            element = bodyElement.addChildElement(new QName("amount"));
            element.addTextNode(Integer.toString(amount));
            element = bodyElement.addChildElement(new QName("channel"));
            element.addTextNode(Integer.toString(channel));
            element = bodyElement.addChildElement(new QName("state"));
            element.addTextNode(state);
            element = bodyElement.addChildElement(new QName("bankreceipt"));
            element.addTextNode(bankReceipt);
            element = bodyElement.addChildElement(new QName("orderid"));
            element.addTextNode(orderId);
            element = bodyElement.addChildElement(new QName("consumer"));
            element.addTextNode(consumer);
            element = bodyElement.addChildElement(new QName("customerip"));
            element.addTextNode(customerIp);
            request.saveChanges();
        } catch (SOAPException e) {
            throw new RuntimeException("soap request creation error", e);
        }

        // send message and get response
        SOAPMessage response = SOAPHelper.callSOAP(request, url);

        // process response
        ISGServiceResponse isgServiceResponse = SOAPHelper.parseResponse(response, namespace, "MCIResponse", ISGServiceResponse.class);

        return isgServiceResponse;
    }
}
