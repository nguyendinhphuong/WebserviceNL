/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jdi.paymentws;

import java.security.MessageDigest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author phuongnd
 */
public class Common {
    public static String md5(String strMd5) {
        try {
            String password = strMd5;
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(password.getBytes());
            byte byteData[] = md.digest();

            //convert the byte to hex format method 1
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < byteData.length; i++) {
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }

            //convert the byte to hex format method 2
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < byteData.length; i++) {
                String hex = Integer.toHexString(0xff & byteData[i]);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception ex) {
            return ex.toString();
        }
    }
    
    public static String getTime() {
        SimpleDateFormat sdp = new SimpleDateFormat(Constants.DATE_FORMAT);
        return sdp.format(new Date());
    }

    public static Date stringToDate(String dateString) {
        try {
            SimpleDateFormat sdp = new SimpleDateFormat(Constants.DATE_FORMAT);
            Date date = sdp.parse(dateString);
            return date;
        } catch (ParseException ex) {
            return null;
        }
    }
   
}
