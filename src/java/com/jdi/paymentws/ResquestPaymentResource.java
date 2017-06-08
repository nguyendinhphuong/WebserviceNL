/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jdi.paymentws;

import com.jdi.paymentws.bean.SendOrderBean;
import com.jdi.paymentws.config.Agent;
import com.jdi.paymentws.config.ServerGW;
import com.jdi.paymentws.dao.DBManager;
import com.jdi.paymentws.logfile.FileManager;
import java.io.IOException;
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
@Path("/requestPayment")
public class ResquestPaymentResource {
    
    DBManager dbm = DBManager.getDbManager();
    FileManager fileManager = FileManager.getFileManager();

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of ResquestPaymentResource
     */
    public ResquestPaymentResource() {
    }

    /**
     * Retrieves representation of an instance of com.jdi.paymentws.ResquestPaymentResource
     * @return an instance of java.lang.String
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getJson() {
        //TODO return proper representation object
        throw new UnsupportedOperationException();
    }

    /**
     * PUT method for updating or creating an instance of ResquestPaymentResource
     * @param content representation for the resource
     * @return 
     * @throws org.json.JSONException
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String postJson(String content) throws JSONException {
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
            //open connection to NL Server
            HttpClient client = HttpClientBuilder.create().build();
            HttpPost post = new HttpPost(agent.getMainUrl());            
            // Bind to sendorderbean            
            SendOrderBean sendOrderBean = new SendOrderBean();
            sendOrderBean.setFunc("sendOrder");
            sendOrderBean.setVersion("1.0");
            sendOrderBean.setMerchantID(agent.getMerchantID());
            sendOrderBean.setMerchantAccount(agent.getMerchantAccount());
            sendOrderBean.setOrderCode("123456DEMO");
            sendOrderBean.setTotalAmount(reqObj.getJSONObject("paymentInfo").getInt("amount"));
            sendOrderBean.setCurrency("vnd");
            sendOrderBean.setLanguage("vi");
            sendOrderBean.setReturnUrl(agent.getReturnUrl());
            sendOrderBean.setCancelUrl(agent.getCancelUrl());
            sendOrderBean.setNotifyUrl(agent.getReturnUrl());
            sendOrderBean.setBuyerFullName(reqObj.getJSONObject("paymentInfo").getString("payer"));
            sendOrderBean.setBuyerEmail(reqObj.getJSONObject("paymentInfo").getString("email"));
            sendOrderBean.setBuyerMobile(reqObj.getJSONObject("paymentInfo").getString("mobile"));
            sendOrderBean.setBuyerAddress(reqObj.getJSONObject("paymentInfo").getString("address"));
            String checksum = getChecksum(sendOrderBean,agent.getMerchantPassword());
            sendOrderBean.setChecksum(checksum);
            //Add parametter
            post.addHeader("User-Agent", "Mozilla/5.0");
            ArrayList<NameValuePair> postParams = new ArrayList<NameValuePair>();
            postParams.add(new BasicNameValuePair("func", sendOrderBean.getFunc()));
            postParams.add(new BasicNameValuePair("version", sendOrderBean.getVersion()));
            postParams.add(new BasicNameValuePair("merchant_id", sendOrderBean.getMerchantID()));
            postParams.add(new BasicNameValuePair("merchant_account", sendOrderBean.getMerchantAccount()));
            postParams.add(new BasicNameValuePair("order_code", sendOrderBean.getOrderCode()));
            postParams.add(new BasicNameValuePair("total_amount", String.valueOf(sendOrderBean.getTotalAmount())));
            postParams.add(new BasicNameValuePair("currency", sendOrderBean.getCurrency()));
            postParams.add(new BasicNameValuePair("language", sendOrderBean.getLanguage()));
            postParams.add(new BasicNameValuePair("return_url", sendOrderBean.getReturnUrl()));
            postParams.add(new BasicNameValuePair("cancel_url", sendOrderBean.getCancelUrl()));
            postParams.add(new BasicNameValuePair("notify_url", sendOrderBean.getNotifyUrl()));
            postParams.add(new BasicNameValuePair("buyer_fullname", sendOrderBean.getBuyerFullName()));
            postParams.add(new BasicNameValuePair("buyer_email", sendOrderBean.getBuyerEmail()));
            postParams.add(new BasicNameValuePair("buyer_mobile", sendOrderBean.getBuyerMobile()));
            postParams.add(new BasicNameValuePair("buyer_address", sendOrderBean.getBuyerAddress()));
            postParams.add(new BasicNameValuePair("checksum", sendOrderBean.getChecksum()));
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
            Logger.getLogger(ResquestPaymentResource.class.getName()).log(Level.SEVERE, null, ex);
            ErrorCode.GW002.toString();
        } catch (IOException ex) {
            Logger.getLogger(ResquestPaymentResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ErrorCode.GW999.toString();
    }
    
    private String getChecksum(SendOrderBean sendOrderBean,String tokenPassword) {
        String stringSendOrder = sendOrderBean.getFunc() + "|" +
                sendOrderBean.getVersion() + "|" +
                sendOrderBean.getMerchantID() + "|" +
                sendOrderBean.getMerchantAccount() + "|" +
                sendOrderBean.getOrderCode() + "|" +
                sendOrderBean.getTotalAmount() + "|" +
                sendOrderBean.getCurrency() + "|" +
                sendOrderBean.getLanguage() + "|" +
                sendOrderBean.getReturnUrl() + "|" +
                sendOrderBean.getCancelUrl() + "|" +
                sendOrderBean.getNotifyUrl() + "|" +
                sendOrderBean.getBuyerFullName() + "|" +
                sendOrderBean.getBuyerEmail() + "|" +
                sendOrderBean.getBuyerMobile() + "|" +
                sendOrderBean.getBuyerAddress() + "|" +
                tokenPassword;
        String checksum = Common.md5(stringSendOrder);

        return checksum;
    }
    
    private boolean validateSignature(JSONObject reqObj, String tokenPassword) throws JSONException{
        StringBuilder sb = new StringBuilder();
        sb.append(reqObj.getString("requestID"));
        sb.append(reqObj.getString("requestTime"));
        JSONObject paymentInfo = reqObj.getJSONObject("paymentInfo");
        sb.append(paymentInfo.getString("payer"));
        sb.append(paymentInfo.getString("amount"));
        sb.append(paymentInfo.getString("email"));
        sb.append(paymentInfo.getString("mobile"));
        sb.append(paymentInfo.getString("address"));
        String signature = Common.md5(sb.toString()+tokenPassword);
        if(signature.equals(reqObj.getString("signature"))) return true;
        else return false;
    }
    
   
    
}
