package com.infotech.isg.ws;

import com.infotech.isg.proxy.SOAPHelper;

import java.util.Set;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.MessageContext;
import javax.xml.namespace.QName;
import javax.servlet.http.HttpServletRequest;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SOAP message handler for ISG Webservice to log input/output messages.
 *
 * @author Sevak Gharibian
 */
@Service
public class ISGWSHandler implements SOAPHandler<SOAPMessageContext> {

    private static final Logger LOG = LoggerFactory.getLogger(ISGWSHandler.class);

    @Override
    public boolean handleMessage(SOAPMessageContext context) {
        if (LOG.isDebugEnabled()) {
            logMessage(context);
        }
        return true;
    }

    @Override
    public boolean handleFault(SOAPMessageContext context) {
        if (LOG.isDebugEnabled()) {
            logMessage(context);
        }
        return true;
    }

    @Override
    public void close(MessageContext context) {
    }

    @Override
    public Set<QName> getHeaders() {
        return null;
    }

    private void logMessage(SOAPMessageContext context) {
        Boolean isResponse = (Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
        HttpServletRequest request = (HttpServletRequest)context.get(MessageContext.SERVLET_REQUEST);
        SOAPMessage message = context.getMessage();
        if (isResponse) {
            LOG.debug("sending to [{}:{}]:{}", request.getRemoteAddr(), request.getRemotePort(), SOAPHelper.toString(message));
        } else {
            LOG.debug("received from [{}:{}]:{}", request.getRemoteAddr(), request.getRemotePort(), SOAPHelper.toString(message));
        }
    }
}
