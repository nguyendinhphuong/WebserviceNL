/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jdi.paymentws.dao;

import com.jdi.paymentws.Constants;
import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author phuongnd
 */
public class DBManager {
    Mongo mongo;
    DB db;
    public static DBManager dbManager; 

    private DBManager() {
        try {
            mongo = new Mongo(Constants.DB_URL, Constants.DB_PORT);
            db = mongo.getDB(Constants.DB_NAME);
        } catch (UnknownHostException ex) {
            Logger.getLogger(DBManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MongoException ex) {
            Logger.getLogger(DBManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static DBManager getDbManager(){
        if(DBManager.dbManager == null) {
             dbManager = new DBManager();
        } 
        return dbManager;
    }
    
    
    public void insertLog(String json){
        LogDBO log = new LogDBO(db);
        log.insert(json);
    }
    
    public void insertPaymentRecord(String json){
        PaymentDBO paymentDBO = new PaymentDBO(db);
        paymentDBO.insert(json);
    }
    

}
