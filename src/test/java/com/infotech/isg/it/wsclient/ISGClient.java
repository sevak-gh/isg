package com.infotech.isg.it.wsclient;

import com.infotech.isg.service.ISGServiceResponse;
import com.infotech.isg.util.SOAPHelper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPException;
import javax.xml.namespace.QName;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.Iterator;
import org.w3c.dom.Node;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.JAXBException;

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
        SOAPMessage request = null;
        try {
            request = MessageFactory.newInstance().createMessage();
            SOAPEnvelope envelope = request.getSOAPPart().getEnvelope();
            envelope.addNamespaceDeclaration("ns", namespace);
            SOAPBody body = request.getSOAPBody();
            request.getMimeHeaders().addHeader("SOAPAction", "\"" + namespace + "/MCI" + "\"");
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
        SOAPMessage response = callSOAP(request);

        // process response
        ISGServiceResponse isgServiceResponse = parseResponse(response, "MCIResponse", ISGServiceResponse.class);

        return isgServiceResponse;
    }

    /**
    * sends soap request and returns soap response.
    */
    private SOAPMessage callSOAP(SOAPMessage request) {
        SOAPMessage response = null;
        SOAPConnection cnn = null;
        try {
            cnn = SOAPConnectionFactory.newInstance().createConnection();
            URL endpoint = new URL(url);
            if (LOG.isDebugEnabled()) {
                LOG.debug("sending to [{}]:{}", url.toString(), SOAPHelper.toString(request));
            }
            response = cnn.call(request, endpoint);
            if (LOG.isDebugEnabled()) {
                LOG.debug("received from [{}]:{}", url.toString(), SOAPHelper.toString(request));
            }
        } catch (SOAPException e) {
            throw new RuntimeException("operator service connection/send/receive error", e);
        } catch (MalformedURLException e) {
            throw new RuntimeException("malformed URL for soap connection", e);
        } finally {
            if (cnn != null) {
                try {
                    cnn.close();
                } catch (SOAPException e) {
                    LOG.error("error closing soap connection, ignorred", e);
                }
            }
        }

        return response;
    }

    /**
    * parses response and returns T
    */
    private <T> T parseResponse(SOAPMessage response, String tagName, Class<T> type) {
        T result = null;
        try {
            SOAPBody responseBody = response.getSOAPBody();
            Iterator iterator = responseBody.getChildElements(new QName(namespace, tagName, "ns"));
            SOAPBodyElement element = (SOAPBodyElement)iterator.next();
            JAXBContext jaxbContext = JAXBContext.newInstance(type);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            result = unmarshaller.unmarshal(element.getFirstChild(), type).getValue();
        } catch (SOAPException e) {
            throw new RuntimeException("soap response error");
        } catch (JAXBException e) {
            throw new RuntimeException("soap response body content unmarshalling error");
        }

        return result;
    }
}
