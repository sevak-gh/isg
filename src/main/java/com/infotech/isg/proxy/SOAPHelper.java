package com.infotech.isg.proxy;

import java.util.Iterator;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import java.net.URL;
import java.net.MalformedURLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * utility methods for SOAP messages
 *
 * throws ProxyAccessException (unchecked) for failure in SOAP connection/send/receive/parse
 * throws RuntimeException for other failures
 *
 * @author Sevak Ghairibian
 */
public class SOAPHelper {

    private static final Logger LOG = LoggerFactory.getLogger(SOAPHelper.class);

    /**
     * creates empty soap request message
     */
    public static SOAPMessage createSOAPRequest(String namespace, String soapAction) {
        SOAPMessage request = null;
        try {
            request = MessageFactory.newInstance().createMessage();
            SOAPEnvelope envelope = request.getSOAPPart().getEnvelope();
            envelope.addNamespaceDeclaration("ns", namespace);
            SOAPBody body = request.getSOAPBody();
            request.getMimeHeaders().addHeader("SOAPAction", "\"" + soapAction + "\"");
            request.saveChanges();
        } catch (SOAPException e) {
            throw new RuntimeException("soap request creation error", e);
        }
        return request;
    }

    /**
     * sends soap request and returns soap response.
     */
    public static SOAPMessage callSOAP(SOAPMessage request, String url) {
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
                LOG.debug("received from [{}]:{}", url.toString(), SOAPHelper.toString(response));
            }
        } catch (SOAPException e) {
            throw new ProxyAccessException("operator service connection/send/receive error", e);
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
    public static <T> T parseResponse(SOAPMessage response, String namespace, String tagName, Class<T> type) {
        T result = null;
        try {
            SOAPBody responseBody = response.getSOAPBody();
            Iterator iterator = responseBody.getChildElements(new QName(namespace, tagName, "ns"));
            if (!iterator.hasNext()) {
                throw new ProxyAccessException("soap response body missing expected item");
            }
            SOAPBodyElement element = (SOAPBodyElement)iterator.next();
            if (element.getFirstChild() == null) {
                throw new ProxyAccessException("soap response body missing expected item");
            }
            JAXBContext jaxbContext = JAXBContext.newInstance(type);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            result = unmarshaller.unmarshal(element.getFirstChild(), type).getValue();
        } catch (SOAPException e) {
            throw new ProxyAccessException("soap response processing error");
        } catch (JAXBException e) {
            throw new ProxyAccessException("soap response body content unmarshalling error");
        }

        return result;
    }

    public static String toString(SOAPMessage message) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("\n");
            if (message.getMimeHeaders().getHeader("Host") != null) {
                sb.append(String.format("Host: %s\n", message.getMimeHeaders().getHeader("Host")));
            }
            if (message.getMimeHeaders().getHeader("Server") != null) {
                sb.append(String.format("Server: %s\n", message.getMimeHeaders().getHeader("Server")));
            }
            if (message.getMimeHeaders().getHeader("Accept") != null) {
                sb.append(String.format("Accept: %s\n", message.getMimeHeaders().getHeader("Accept")));
            }
            if (message.getMimeHeaders().getHeader("Accept-Encoding") != null) {
                sb.append(String.format("Accept-Encoding: %s\n", message.getMimeHeaders().getHeader("Accept-Encoding")));
            }
            if (message.getMimeHeaders().getHeader("Content-Encoding") != null) {
                sb.append(String.format("Content-Encoding: %s\n", message.getMimeHeaders().getHeader("Content-Encoding")));
            }
            if (message.getMimeHeaders().getHeader("Content-Type") != null) {
                sb.append(String.format("Content-Type: %s\n", message.getMimeHeaders().getHeader("Content-Type")));
            }
            if (message.getMimeHeaders().getHeader("Content-Length") != null) {
                sb.append(String.format("Content-Length: %s\n", message.getMimeHeaders().getHeader("Content-Length")));
            }
            if (message.getMimeHeaders().getHeader("SOAPAction") != null) {
                sb.append(String.format("SOAPAction: %s\n", message.getMimeHeaders().getHeader("SOAPAction")));
            }
            if (message.getMimeHeaders().getHeader("Date") != null) {
                sb.append(String.format("Date: %s\n", message.getMimeHeaders().getHeader("Date")));
            }
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            message.writeTo(output);
            sb.append(output);
            return sb.toString();
        } catch (SOAPException e) {
            throw new RuntimeException("error creating string representation of soap message", e);
        } catch (IOException e) {
            throw new RuntimeException("error creating string representation of soap message", e);
        }
    }
}
