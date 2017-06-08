/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jdi.paymentws;

/**
 *
 * @author phuongnd
 */
public enum ErrorCode {   

  
  GW001("GW001", "User agent không tồn tại."),
  GW002("GW002", "Xâu dữ liệu JSON không hợp lệ."),
  GW003("GW003", "RequestId không hợp lệ."),
  GW004("GW004", "requestTime không đúng."),
  GW005("GW005", "Chữ ký không hợp lệ."),
  GW999("GW999", "Lỗi không xác định.");

  private final String code;
  private final String description;

  private ErrorCode(String code, String description) {
    this.code = code;
    this.description = description;
  }

  public String getDescription() {
     return description;
  }

  public String getCode() {
     return code;
  }

  @Override
  public String toString() {
    return "{ response_code:\"" + code + "\",response_detail:\""+ description + "\"}";
  }  
  
}

