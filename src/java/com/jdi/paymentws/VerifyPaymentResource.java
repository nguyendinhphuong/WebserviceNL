/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jdi.paymentws;

import com.jdi.paymentws.bean.CheckOrderBean;
import com.jdi.paymentws.config.Agent;
import com.jdi.paymentws.config.ServerGW;
import com.jdi.paymentws.dao.DBManager;
import com.jdi.paymentws.logfile.FileManager;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * REST Web Service
 *
 * @author phuongnd
 */
@Path("verifyPayment")
public class VerifyPaymentResource {
    
    DBManager dbm = DBManager.getDbManager();
    FileManager fileManager = FileManager.getFileManager();

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of VerifyPaymentResource
     */
    public VerifyPaymentResource() {
    }

    /**
     * Retrieves representation of an instance of com.jdi.paymentws.VerifyPaymentResource
     * @return an instance of java.lang.String
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getJson() {
        //TODO return proper representation object
        throw new UnsupportedOperationException();
    }

    /**
     * PUT method for updating or creating an instance of VerifyPaymentResource
     * @param content representation for the resource
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String putJson(String content) {
        try {
            JSONObject reqObj = new JSONObject(content);
            reqObj.put("GWReceivedTime", Common.getTime());
            System.out.print("JSON:"+reqObj);
             //validate requestID
            if("".equals(reqObj.getString("requestID"))){
                return ErrorCode.GW003.toString();
            }
            //validate requestTime
            if(Common.stringToDate(reqObj.getString("requestTime"))==null){
                return ErrorCode.GW004.toString();
            }
            //validate agent            
            Agent agent = ServerGW.getServerGW().getAgentByName(reqObj.getString("agentUser"));
            if(agent == null) return ErrorCode.GW001.toString();
            //validate signature
            if(!validateSignature(reqObj,agent.getAgentPassword())) 
            return ErrorCode.GW005.toString();
            // build client
            HttpClient client = HttpClientBuilder.create().build();
            HttpPost post = new HttpPost(agent.getMainUrl());
            
            
            CheckOrderBean checkOrderBean = new CheckOrderBean();
            checkOrderBean.setFunc("checkOrder");
            checkOrderBean.setVersion("1.0");
            checkOrderBean.setMerchantID(agent.getMerchantID());
            checkOrderBean.setTokenCode(reqObj.getString("token"));

            String checksum = getVerifyPaymentChecksum(checkOrderBean,agent.getMerchantPassword());
            checkOrderBean.setChecksum(checksum);
            
            // add request header & params
            post.addHeader("User-Agent", "Mozilla/5.0");
            ArrayList<NameValuePair> postParams = new ArrayList<NameValuePair>();
            postParams.add(new BasicNameValuePair("func",checkOrderBean.getFunc()));
            postParams.add(new BasicNameValuePair("version",checkOrderBean.getVersion()));
            postParams.add(new BasicNameValuePair("merchant_id",checkOrderBean.getMerchantID()));
            postParams.add(new BasicNameValuePair("token_code",checkOrderBean.getTokenCode()));
            postParams.add(new BasicNameValuePair("checksum",checkOrderBean.getChecksum()));
            
            post.setEntity(new UrlEncodedFormEntity(postParams));
            
            reqObj.put("GW2NLSendTime", Common.getTime());            
//            dbm.insertLog(reqObj.toString());
            System.out.println(reqObj.toString());
            HttpResponse response = client.execute(post);
            
            String responseString;
            responseString = new BasicResponseHandler().handleResponse(response);
            reqObj.put("GW2NLReceivedTime", Common.getTime());
            dbm.insertLog(reqObj.toString());
            fileManager.getLogFile().append(reqObj.toString());
            System.out.println("Response Code : "
                            + responseString);
                    
            return responseString;
        
            
            
        } catch (JSONException ex) {
            Logger.getLogger(VerifyPaymentResource.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(VerifyPaymentResource.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(VerifyPaymentResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    private String getVerifyPaymentChecksum(CheckOrderBean checkOrderBean, String tokenPassword) {
        String stringSendOrder = checkOrderBean.getFunc() + "|" +
                checkOrderBean.getVersion() + "|" +
                checkOrderBean.getMerchantID() + "|" +
                checkOrderBean.getTokenCode() + "|" +
                tokenPassword;
        String checksum = Common.md5(stringSendOrder);

        return checksum;
    }
    
    private boolean validateSignature(JSONObject reqObj, String tokenPassword) throws JSONException{
        StringBuilder sb = new StringBuilder();
        sb.append(reqObj.getString("requestID"));
        sb.append(reqObj.getString("requestTime"));
        sb.append(reqObj.getString("agentUser"));
        sb.append(reqObj.getString("token"));       
        String signature = Common.md5(sb.toString()+tokenPassword);
        if(signature.equals(reqObj.getString("signature"))) return true;
        else return false;
    }    
}