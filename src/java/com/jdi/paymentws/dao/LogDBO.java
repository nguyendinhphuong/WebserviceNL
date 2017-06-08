/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jdi.paymentws.dao;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

/**
 *
 * @author phuongnd
 */
public class LogDBO {
    DB db;
    DBCollection collection;

    public LogDBO(DB db) {
        this.db = db;
        this.collection = db.getCollection("logPayment");
    }
    
    public void insert(String json) {
        DBObject dbObject = (DBObject)JSON.parse(json);        
        collection.insert(dbObject);
    }
}
