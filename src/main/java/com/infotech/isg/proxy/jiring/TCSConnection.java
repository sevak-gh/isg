package com.infotech.isg.proxy.jiring;

import com.infotech.isg.proxy.ProxyAccessException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.StringReader;
import javax.xml.transform.stream.StreamSource;
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.DataOutputStream;
import java.io.BufferedReader;
import java.util.Map;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * makes TCS connection, sends request and receives response
 *
 * throws ProxyAccessException (unchecked) for failure in connection/send/receive/parse
 * throws RuntimeException for other failures

 * @author Sevak Gharibian
 */
public class TCSConnection {

    private static final Logger LOG = LoggerFactory.getLogger(TCSConnection.class);

    public static TCSResponse call(TCSRequest request, String host) {
        String data = marshal(request, TCSRequest.class);
        HttpURLConnection cnn = null;
        BufferedReader reader = null;
        DataOutputStream writer = null;
        try {
            URL url = new URL(host);
            cnn = (HttpURLConnection)url.openConnection();
            cnn.setRequestMethod("POST");
            cnn.setDoOutput(true);
            cnn.setRequestProperty("Content-Type", "text/xml");
            cnn.setRequestProperty("Content-Length", Integer.toString(data.length()));
            cnn.connect();
            //if (LOG.isDebugEnabled()) {
            //    StringBuilder sb = new StringBuilder();
            /*
            Map<String, List<String>> map = cnn.getHeaderFields();
            for (Map.Entry<String, List<String>> entry : map.entrySet()) {
                sb.append(String.format("%s: %s\n", entry.getKey(), entry.getValue()));
            }
            */
            /*
            sb.append("POST\n");
            sb.append("Content-Type: text/xml\n");
            sb.append(String.format("Content-Length: %d\n", data.length()));
            */
            //    sb.append(data);
            //    LOG.debug("sending to [{}]:{}", url.toString(), sb.toString());
            //}
            writer = new DataOutputStream(cnn.getOutputStream());
            writer.write(data.getBytes(), 0, data.length());
            writer.flush();
            writer.close();
            reader = new BufferedReader(new InputStreamReader(cnn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            /*
            if (LOG.isDebugEnabled()) {
                StringBuilder sb = new StringBuilder();
                Map<String, List<String>> map = cnn.getHeaderFields();
                for (Map.Entry<String, List<String>> entry : map.entrySet()) {
                    sb.append(String.format("%s: %s\n", entry.getKey(), entry.getValue()));
                }
                sb.append(response.toString());
                LOG.debug("received from [{}]:{}", url.toString(), sb.toString());
            }
            */
            return unmarshal(response.toString(), TCSResponse.class);
        } catch (IOException e) {
            throw new ProxyAccessException("TCS operator connection/send/receive error", e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    LOG.error("error while closing reader in TCS connection", e);
                }
            }
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    LOG.error("error while closing writer in TCS connection", e);
                }
            }
            if (cnn != null) {
                cnn.disconnect();
            }
        }
    }

    public static <T> String marshal(Object data, Class<T> type) {
        try {
            JAXBContext context = JAXBContext.newInstance(type);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            StringWriter writer = new StringWriter();
            marshaller.marshal(data, writer);
            LOG.debug("object of type {} marshalled to : \n{}", type.toString(), writer.toString());
            return writer.toString();
        } catch (JAXBException e) {
            throw new ProxyAccessException("error while marshalling " + type.toString(), e);
        }
    }

    public static <T> T unmarshal(String data, Class<T> type) {
        try {
            JAXBContext context = JAXBContext.newInstance(type);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            T object = unmarshaller.unmarshal(new StreamSource(new StringReader(data)), type).getValue();
            LOG.debug("type {} unmarshalled from : \n{}", type.toString(), data);
            return object;
        } catch (JAXBException e) {
            throw new ProxyAccessException("error while unmarshalling " + type.toString(), e);
        }
    }
}
