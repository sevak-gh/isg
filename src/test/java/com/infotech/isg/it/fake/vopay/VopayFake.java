package com.infotech.isg.it.fake.vopay;

import com.infotech.isg.proxy.vopay.VopayProxy;
import com.infotech.isg.proxy.vopay.VopayProxyAccountInfoResponse;
import com.infotech.isg.proxy.vopay.VopayProxyAvailablePackagesResponse;
import com.infotech.isg.proxy.vopay.VopayProxyPerformTransactionResponse;

import java.util.List;
import java.util.ArrayList;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.FormParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.MediaType;
import java.math.BigDecimal;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import com.sun.net.httpserver.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * fake web service for Vopay, used for integration tests
 *
 * @author Sevak Gharibian
 */
@Path("/")
public class VopayFake {

    private static final Logger LOG = LoggerFactory.getLogger(VopayFake.class);

    private final VopayProxy vopayService;
    private final String url;

    private HttpServer server = null;

    public VopayFake(VopayProxy vopayService, String url) {
        this.vopayService = vopayService;
        this.url = url;
    }

    public void start() {
        ResourceConfig config = new ResourceConfig();
        config.registerInstances(this);
        URI uri = UriBuilder.fromUri(url).build();
        server = JdkHttpServerFactory.createHttpServer(uri, config);
    }    
    
    public void stop() {
        if (server != null) {
            server.stop(0);
            server = null;
        }
    }

    @POST
    @Path("/account_info")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public VopayProxyAccountInfoResponse accountInfo(@FormParam("AccountID") String accountId,
                                                     @FormParam("AgentID") String agentId,
                                                     @FormParam("Key") String key,
                                                     @FormParam("Signature") String signature) {
        return vopayService.accountInfo();
    }

    @POST
    @Path("/available_packages")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public VopayProxyAvailablePackagesResponse availablePakcgaes(@FormParam("AccountID") String accountId, 
                                                                 @FormParam("AgentID") String agentId,
                                                                 @FormParam("Key") String key,
                                                                 @FormParam("Signature") String signature,
                                                                 @FormParam("PhoneNumber") String phoneNumber) {

        return vopayService.availablePackages(phoneNumber);
    }

    @POST
    @Path("/perform_transaction")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public VopayProxyPerformTransactionResponse performTransaction(@FormParam("AccountID") String accountId, 
                                                                   @FormParam("AgentID") String agentId,
                                                                   @FormParam("Key") String key,
                                                                   @FormParam("Signature") String signature,
                                                                   @FormParam("RecipientPhoneNumber") String phoneNumber,
                                                                   @FormParam("Package") String packageName,
                                                                   @FormParam("SenderName") String senderName,
                                                                   @FormParam("SenderPhoneNumber") String senderPhoneNumber,
                                                                   @FormParam("SenderEmail") String senderEmail,
                                                                   @FormParam("SenderMessage") String senderMessage) {
        return vopayService.performTransaction(phoneNumber, packageName, senderName,
                                               senderPhoneNumber, senderEmail, senderMessage);
    }
}
