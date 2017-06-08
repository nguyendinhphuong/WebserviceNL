/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jdi.paymentws.listener;

import com.jdi.paymentws.config.Agent;
import com.jdi.paymentws.config.ServerGW;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

/**
 *
 * @author phuongnd
 */
public class PGWServletContextListener implements ServletContextListener{

    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("Servlet context listener started!");
        this.loadConfig();
    }

    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("Servlet context listener destroy!");
    }
    
    public void loadConfig(){
        try {
           SAXReader reader = new SAXReader();
            File configFile = new File("/home/phuongnd/NetBeansProjects/RestPaymentWS/config.properties");            
            Document document = reader.read(configFile);             
            ServerGW servergw = ServerGW.getServerGW();
            servergw.setLogDuration(Integer.parseInt(document.selectSingleNode("/root/logDuration").getText()));
            servergw.setLogFilename(document.selectSingleNode("/root/logFilename").getText());
            servergw.setLogFolder(document.selectSingleNode("/root/logFolder").getText());
            List<Node> nodes = document.selectNodes("/root/agents/agent");
            for(Node na: nodes){
                Agent ag = new Agent();
                ag.setAgentPassword(na.selectSingleNode("agentPassword").getText());
                ag.setMainUrl(na.selectSingleNode("mainUrl").getText());
                ag.setReturnUrl(na.selectSingleNode("returnUrl").getText());
                ag.setCancelUrl(na.selectSingleNode("cancelUrl").getText());
                ag.setMerchantID(na.selectSingleNode("merchantID").getText());
                ag.setMerchantPassword(na.selectSingleNode("merchantPassword").getText());
                ag.setMerchantAccount(na.selectSingleNode("merchantAccount").getText());                
                servergw.putAgent(((Element) na).attributeValue("name"), ag);
            }
        }  catch (DocumentException ex) {
            Logger.getLogger(PGWServletContextListener.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }    
     
    
}