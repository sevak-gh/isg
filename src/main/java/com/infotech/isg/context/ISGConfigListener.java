package com.infotech.isg.context;

import java.io.FileInputStream;
import java.io.IOException;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletContextEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
* context listener for loading isg.ini as system properties.
*
* @author Sevak Gharibian
*/
public class ISGConfigListener implements ServletContextListener {

    private static final String INI_FILE_PATH = "/etc/isg/isg.ini";

    private static final Logger LOG = LoggerFactory.getLogger(ISGConfigListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            System.getProperties().load(new FileInputStream(INI_FILE_PATH));
            LOG.debug("properties file: {} loaded successfully", INI_FILE_PATH);
        } catch (IOException e) {
            throw new RuntimeException("error in loading properties: " + INI_FILE_PATH, e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}
