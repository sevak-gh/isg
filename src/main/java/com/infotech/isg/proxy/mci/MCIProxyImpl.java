package com.infotech.isg.proxy.mci;

import com.infotech.isg.validation.ErrorCodes;
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

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Required;

/**
* implementation for MCI proxy.
*
* @author Sevak Gharibian
*/
@Component("MCIProxy")
public class MCIProxyImpl implements MCIProxy {

    @Value("${mci.url}")
    private String url;

    @Value("${mci.username}")
    private String username;

    @Value("${mci.password}")
    private String password;

    @Value("${mci.namespace}")
    private String namespace;

    private static final String SOAPACTION_GETTOKEN = "GetToken";
    private static final String SOAPACTION_RECHARGE = "Recharge";

    public void setUrl(String url) {
        this.url = url;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    /**
    * creates empty soap request message
    */
    private SOAPMessage createSOAPRequest(String soapAction) {
        SOAPMessage request = null;
        try {
            request = MessageFactory.newInstance().createMessage();
            SOAPEnvelope envelope = request.getSOAPPart().getEnvelope();
            envelope.addNamespaceDeclaration("ns", namespace);
            SOAPBody body = request.getSOAPBody();
            request.getMimeHeaders().addHeader("SOAPAction", soapAction);
            request.saveChanges();
        } catch (SOAPException e) {
            throw new RuntimeException("soap request creation error", e);
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
            throw new ISGException(ErrorCodes.OPERATOR_SERVICE_ERROR, "operator service connection/send/receive error", e);
        } catch (MalformedURLException e) {
            throw new RuntimeException("malformed URL for soap connection", e);
        } finally {
            if (cnn != null) {
                try {
                    cnn.close();
                } catch (SOAPException e) {
                    //TODO: just log this, do not throw
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
            throw new ISGException(ErrorCodes.OPERATOR_SERVICE_ERROR, "soap response error");
        } catch (JAXBException e) {
            throw new ISGException(ErrorCodes.OPERATOR_SERVICE_ERROR, "soap response body content unmarshalling error");
        }

        return result;
    }

    @Override
    public MCIProxyGetTokenResponse getToken() {

        // create empty soap request
        SOAPMessage request = createSOAPRequest(url + SOAPACTION_GETTOKEN);

        // add request body/header

        // send message and get response
        SOAPMessage response = callSOAP(request);

        // process response
        MCIProxyGetTokenResponse getTokenResponse = parseResponse(response, "GetTokenResponse", MCIProxyGetTokenResponse.class);

        return getTokenResponse;
    }

    @Override
    public MCIProxyRechargeResponse recharge(String token, String consumer,
            int amount, long trId) {

        // create empty soap request
        SOAPMessage request = createSOAPRequest(url + SOAPACTION_RECHARGE);

        // add request body/header
        try {
            request.getMimeHeaders().addHeader("SOAPAction", SOAPACTION_RECHARGE);
            SOAPHeader header = request.getSOAPHeader();
            SOAPHeaderElement headerElement = header.addHeaderElement(new QName(namespace, "AuthHeader", "ns"));
            SOAPElement usernameElement = headerElement.addChildElement(new QName(namespace, "UserName", "ns"));
            usernameElement.setValue(username);
            SOAPElement passwordElement = headerElement.addChildElement(new QName(namespace, "Password", "ns"));
            String combination = username.toUpperCase() + "|" + password + "|" + token;
            passwordElement.setValue(HashGenerator.getMD5(combination));
            SOAPBody body = request.getSOAPBody();
            SOAPBodyElement bodyElement = body.addBodyElement(new QName(namespace, "Recharge", "ns"));
            SOAPElement element = bodyElement.addChildElement(new QName(namespace, "BrokerID", "ns"));
            element.addTextNode(username);
            element = bodyElement.addChildElement(new QName(namespace, "MobileNumber", "ns"));
            element.addTextNode(consumer);
            element = bodyElement.addChildElement(new QName(namespace, "CardAmount", "ns"));
            element.addTextNode(Integer.toString(amount));
            element = bodyElement.addChildElement(new QName(namespace, "TransactionID", "ns"));
            element.addTextNode("MCI" + Long.toString(trId));
            request.saveChanges();
        } catch (SOAPException e) {
            throw new RuntimeException("soap extended request creation error", e);
        }

        // send message and get response
        SOAPMessage response = callSOAP(request);

        // process response
        MCIProxyRechargeResponse rechargeResponse = parseResponse(response, "RechargeReponse", MCIProxyRechargeResponse.class);

        return rechargeResponse;
    }
}
