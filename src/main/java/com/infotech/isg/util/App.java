package com.infotech.isg.util;

import java.util.List;

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
    }
}
