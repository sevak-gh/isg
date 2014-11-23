package com.infotech.isg.proxy.mci;

import com.infotech.isg.service.ErrorCodes;
import com.infotech.isg.service.ISGException;
import com.infotech.isg.util.HashGenerator;

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

/**
* implementation for MCI proxy.
*
* @author Sevak Gharibian
*/
public class MCIProxyImpl implements MCIProxy {

    private String url;
    private static final String NAMESPACE = "http:/10.20.8.210:4001/";
    private static final String SOAPACTION_GETTOKEN = "http://10.20.8.210:4001/GetToken";
    private static final String SOAPACTION_RECHARGE = "http://10.20.8.210:4001/Recharge";

    public MCIProxyImpl(String url) {
        this.url = url;
    }

    /**
    * creates empty soap request message
    */
    private SOAPMessage createSOAPRequest(String soapAction) {
        SOAPMessage request = null;
        try {
            request = MessageFactory.newInstance().createMessage();
            SOAPEnvelope envelope = request.getSOAPPart().getEnvelope();
            envelope.addNamespaceDeclaration("ns", NAMESPACE);
            SOAPBody body = request.getSOAPBody();
            request.getMimeHeaders().addHeader("SOAPAction", soapAction);
            request.saveChanges();
        } catch (SOAPException e) {
            throw new RuntimeException(e);
        }
        return request;
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
            response = cnn.call(request, endpoint);
        } catch (SOAPException e) {
            throw new ISGException(ErrorCodes.OPERATOR_SERVICE_UNAVAILABLE, "operator service not available", e);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } finally {
            if (cnn != null) {
                try {
                    cnn.close();
                } catch (SOAPException e) {
                    throw new RuntimeException(e);
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
            Iterator iterator = responseBody.getChildElements(new QName(NAMESPACE, tagName, "ns"));
            SOAPBodyElement element = (SOAPBodyElement)iterator.next();
            JAXBContext jaxbContext = JAXBContext.newInstance(type);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            result = unmarshaller.unmarshal(element.getFirstChild(), type).getValue();
        } catch (SOAPException e) {
            throw new ISGException(ErrorCodes.OPERATOR_SERVICE_ERROR, "operator service error");
        } catch (JAXBException e) {
            throw new ISGException(ErrorCodes.OPERATOR_SERVICE_ERROR, "operator service error");
        }

        return result;
    }

    @Override
    public MCIProxyGetTokenResponse getToken() {

        // create empty soap request
        SOAPMessage request = createSOAPRequest(SOAPACTION_GETTOKEN);

        // add request body/header

        // send message and get response
        SOAPMessage response = callSOAP(request);

        // process response
        MCIProxyGetTokenResponse getTokenResponse = parseResponse(response, "GetTokenResponse", MCIProxyGetTokenResponse.class);

        return getTokenResponse;
    }

    @Override
    public MCIProxyRechargeResponse recharge(String token, String username, String password,
            String consumer, int amount, long trId) {

        // create empty soap request
        SOAPMessage request = createSOAPRequest(SOAPACTION_RECHARGE);

        // add request body/header
        try {
            request.getMimeHeaders().addHeader("SOAPAction", SOAPACTION_RECHARGE);
            SOAPHeader header = request.getSOAPHeader();
            SOAPHeaderElement headerElement = header.addHeaderElement(new QName(NAMESPACE, "AuthHeader", "ns"));
            SOAPElement usernameElement = headerElement.addChildElement(new QName(NAMESPACE, "UserName", "ns"));
            usernameElement.setValue(username);
            SOAPElement passwordElement = headerElement.addChildElement(new QName(NAMESPACE, "Password", "ns"));
            String combination = username.toUpperCase() + "|" + password + "|" + token;
            passwordElement.setValue(HashGenerator.getMD5(combination));
            SOAPBody body = request.getSOAPBody();
            SOAPBodyElement bodyElement = body.addBodyElement(new QName(NAMESPACE, "Recharge", "ns"));
            SOAPElement element = bodyElement.addChildElement(new QName(NAMESPACE, "BrokerID", "ns"));
            element.addTextNode(username);
            element = bodyElement.addChildElement(new QName(NAMESPACE, "MobileNumber", "ns"));
            element.addTextNode(consumer);
            element = bodyElement.addChildElement(new QName(NAMESPACE, "CardAmount", "ns"));
            element.addTextNode(Integer.toString(amount));
            element = bodyElement.addChildElement(new QName(NAMESPACE, "TransactionID", "ns"));
            element.addTextNode("MCI" + Long.toString(trId));
            request.saveChanges();
        } catch (SOAPException e) {
            throw new RuntimeException(e);
        }

        // send message and get response
        SOAPMessage response = callSOAP(request);

        // process response
        MCIProxyRechargeResponse rechargeResponse = parseResponse(response, "RechargeReponse", MCIProxyRechargeResponse.class);

        return rechargeResponse;
    }
}
