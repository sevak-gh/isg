package com.infotech.isg.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPException;

/**
* utility methods for SOAP messages
*
* @author Sevak Ghairibian
*/
public class SOAPHelper {

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
