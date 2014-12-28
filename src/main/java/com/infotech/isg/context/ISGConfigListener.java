package com.infotech.isg.context;

import java.util.Properties;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletContextEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;

/**
* context listener for loading isg.ini as system properties.
*
* @author Sevak Gharibian
*/
public class ISGConfigListener implements ServletContextListener {

    private static final String INI_FILE_PATH = "/etc/isg/isg.ini";

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        InputStream input = null;
        try {
            input = new FileInputStream(INI_FILE_PATH);
            Properties properties = new Properties(System.getProperties());
            properties.load(input);
            System.setProperties(properties);
            LoggerFactory.getLogger(ISGConfigListener.class).debug("properties file: {} loaded successfully", INI_FILE_PATH);
        } catch (IOException e) {
            throw new RuntimeException("error in loading properties from file: " + INI_FILE_PATH, e);
        } finally {
            try {
                if (input != null) {
                    input.close();
                }
            } catch (IOException e) {
                //TODO: there is nothing to de here!!!
            }
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}
