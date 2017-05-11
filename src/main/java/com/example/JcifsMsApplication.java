package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileOutputStream;

@SpringBootApplication
public class JcifsMsApplication {

	static final String NAS_FOLDER = "smb://192.168.11.8/test";
	static final String USER_NAME = "user1";
	static final String PASSWORD = "pw";
	
	public static void main(String[] args) {
		SpringApplication.run(JcifsMsApplication.class, args);
		
		//JcifsMsApplication.copyFiles();
	}
	
	static boolean copyFiles() {
		
		String fileName="a.txt",  fileContent="hello", err = "";
		
        boolean successful = false;
        SmbFileOutputStream sfos = null;
        
         try{
                String user = USER_NAME + ":" + PASSWORD;
                System.out.println("User: " + user);
                System.out.println("fileName[" + fileName + "] fileContent[" + fileContent + "]");
 
                NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(user);
                String filePath = NAS_FOLDER + "/" + fileName;
                System.out.println("Path: " + filePath);
 
                SmbFile sFile = new SmbFile(filePath, auth);
                //SmbFile sFile = new SmbFile(filePath);
                sfos = new SmbFileOutputStream(sFile, true);
                sfos.write(fileContent.getBytes());
                
                successful = true;
                System.out.println("Successful");
            } catch (Exception e) {
                e.printStackTrace();
                err = e.toString();
            }
         	finally {
         		if (sfos != null)
         		{
         			try {
         				sfos.close();
         			}
         			catch(Exception e) {}
         		}
         	}
        return successful;
    }
}
