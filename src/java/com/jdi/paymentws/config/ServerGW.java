/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jdi.paymentws.config;

import java.util.HashMap;

/**
 *
 * @author phuongnd
 */
public class ServerGW {
    
    private HashMap<String,Agent> listAgent = new HashMap<String, Agent>();
    private String logFolder;
    private String logFilename;
    private int logDuration;
    private static ServerGW servergw;

    private ServerGW() {       
    }

    public static ServerGW getServerGW(){
        if(servergw==null){
            servergw = new ServerGW();
        }
        return servergw;
    }
    

    public HashMap<String, Agent> getListAgent() {
        return listAgent;
    }

    public void setListAgent(HashMap<String, Agent> listAgent) {
        this.listAgent = listAgent;
    }

    public String getLogFolder() {
        return logFolder;
    }

    public void setLogFolder(String logFolder) {
        this.logFolder = logFolder;
    }

    public String getLogFilename() {
        return logFilename;
    }

    public void setLogFilename(String logFilename) {
        this.logFilename = logFilename;
    }

    public int getLogDuration() {
        return logDuration;
    }

    public void setLogDuration(int logDuration) {
        this.logDuration = logDuration;
    }
    
    public Agent getAgentByName(String name){
        return listAgent.get(name);
    }

    public void putAgent(String name, Agent agent){
        this.listAgent.put(name, agent);
    }
    
    
}