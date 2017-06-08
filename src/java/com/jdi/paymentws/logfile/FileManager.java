/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jdi.paymentws.logfile;

import com.jdi.paymentws.Constants;
import com.jdi.paymentws.config.ServerGW;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *  Tao, quan ly file theo thoi gian
 * @author phuongnd
 */
public class FileManager {
    
    private LogFile currentLogFile;
    private final String folder;
    private final int duration; // khoang thoi gian ghi log cua 1 file ms
    private final String fileName;
    private static FileManager fileManager;

    private FileManager(String folder,int duration, String fileName) {        
        this.folder = folder;
        this.duration = duration;
        this.fileName = fileName;
    }

    public static FileManager getFileManager(){
        if(fileManager==null) {
            fileManager = new FileManager(
                    ServerGW.getServerGW().getLogFolder(),
                    ServerGW.getServerGW().getLogDuration(),
                    ServerGW.getServerGW().getLogFilename());
        }
        return fileManager;
    }
    
    
    private void createFile(String folder){
        if(currentLogFile!=null){
            rename();
        }
        File file = new File(this.folder + File.separator + fileName + getDateString() + ".temp");
        currentLogFile = new LogFile(file, System.currentTimeMillis());
    }
    
    public LogFile getLogFile(){
        if(currentLogFile==null) {
            this.createFile(folder);
        } else if(this.currentLogFile.createTime+duration < System.currentTimeMillis()) {
            this.createFile(folder);
        }
        return currentLogFile;
    }
    
    private String getDateString(){
        SimpleDateFormat sdf = new SimpleDateFormat("YYYYMMDDHHmmss");
        return sdf.format(new Date());
    }
    
    private void rename(){
        try {
            currentLogFile.close();
            String oldName = currentLogFile.file.getAbsolutePath();
            String newName = oldName.replaceFirst(".temp", ".txt");
            File newFile = new File(newName);
            currentLogFile.file.renameTo(newFile);
            System.out.println("Rename" + oldName);
        } catch (IOException ex) {
            Logger.getLogger(FileManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    
}
