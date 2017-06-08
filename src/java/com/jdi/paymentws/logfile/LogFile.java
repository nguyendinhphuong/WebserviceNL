/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jdi.paymentws.logfile;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author phuongnd
 */
public class LogFile {
    File file;
    long createTime;
    FileWriter fout;

    public LogFile(File file, long createTime) {
        this.file = file;
        this.createTime = createTime;
    }

    synchronized public void append(String log) throws IOException{        
        if(fout == null) {
            fout = new FileWriter(file, true);
        }
        fout.write(log+System.lineSeparator());
    }
    
    public void close() throws IOException{
        fout.close();
    }
    
    
}
