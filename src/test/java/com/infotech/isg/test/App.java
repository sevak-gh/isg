package com.infotech.isg.test;

import java.util.List;
import java.util.Date;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.infotech.isg.domain.*;
import com.infotech.isg.repository.*;
import com.infotech.isg.repository.jdbc.*;

/**
* app for testing container-less
*
* @author Sevak Gahribian
*/
public class App {

    public static void main(String[] args) {
        new App().go();
    }

    private void go() {
        System.out.println("isg test app...");

        ApplicationContext context = new ClassPathXmlApplicationContext("spring/applicationContext.xml");
        ClientRepository clientRepo = context.getBean("ClientRepositoryJdbc", ClientRepository.class);
        Client client = clientRepo.findByUsername("test");
        System.out.println("id: " + Integer.toString(client.getId()));
        System.out.println("username: " + client.getUsername());
        System.out.println("password: " + client.getPassword());
        System.out.println("active: " + Boolean.toString(client.getIsActive()));
        List<String> ips = client.getIps();
        System.out.println("ips......");
        for (String ip : ips) {
            System.out.println(ip);
        }

        TransactionRepository transactionRepo = context.getBean("TransactionRepositoryJdbc", TransactionRepository.class);
        Transaction transaction = null;
        /*
        transaction = new Transaction();
        transaction.setProvider(Operator.MCI_ID);
        transaction.setAction(ServiceActions.TOP_UP);
        transaction.setState("trstate");
        transaction.setResNum("res123456");
        transaction.setRefNum("ref123456");
        transaction.setRemoteIp("10.30.180.38");
        transaction.setAmount(10000);
        transaction.setChannel(54);
        transaction.setConsumer("cnsmr5566");
        transaction.setBankCode(BankCodes.SAMAN);
        transaction.setClientId(3);
        transaction.setCustomerIp("10.1.1.1");
        transaction.setTrDateTime(new Date());
        transaction.setStatus(-1);
        transaction.setBankVerify(new Integer(10000));
        transaction.setVerifyDateTime(new Date());
        transactionRepo.create(transaction);
        */
        transaction = transactionRepo.findByRefNumBankCodeClientId("ref123456", BankCodes.SAMAN, 3);
        if (transaction != null) {
            System.out.println(String.format("transaction[%d]:%s,%s,%d,%d,%s", transaction.getId(),
                                             transaction.getRefNum(), transaction.getResNum(),
                                             transaction.getProvider(), transaction.getAction(),
                                             transaction.getRemoteIp()));
        } else {
            System.out.println("transaction not found!");
        }
    }
}
