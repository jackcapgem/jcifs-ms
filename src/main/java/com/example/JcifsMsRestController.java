package com.example;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;

import javax.websocket.server.PathParam;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import jcifs.smb.*;

@RestController
@RequestMapping("/api")
@ConfigurationProperties(prefix="jcifs-ms")
public class JcifsMsRestController {
	
	private String sambaPath;
	private String user;
	private String password;
	
	@RequestMapping(method = RequestMethod.GET, value = "/ping", produces = "text/plain")
	public String ping()
	{
		System.out.println("Received ping request");
		return "Response at: " + LocalDateTime.now().toString();
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/copy", params={"content", "filename"}, produces = "text/plain")
	public String copy(@PathParam("content") String content, @PathParam("filename") String filename) throws UnknownHostException {
		String hostname = null;
		try {
			hostname = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			hostname = "unknown";
		}
		
		String retStr = "JcifsMsRestController at [" + hostname + "] user[" + this.user + "]; copied filename[" + filename + "] content[" + content + "] to [" + sambaPath +"]\n";
		StringBuilder err = new StringBuilder();
		if (copyFiles(filename, content, err))
			return retStr + "Success";
		else
			return retStr + "Failed - " + err.toString();
	}
	
	public String getSambaPath()
	{
		return sambaPath;
	}
	
	public void setSambaPath(String sambaPath)
	{
		this.sambaPath = sambaPath;
	}
	
	public String getUser()
	{
		return user;
	}
	
	public void setUser(String user)
	{
		this.user = user;
	}
	
	public String getPassword()
	{
		return password;
	}
	
	public void setPassword(String password)
	{
		this.password = password;
	}
	
	private boolean copyFiles(String fileName, String fileContent, StringBuilder err) {
        boolean successful = false;
        SmbFileOutputStream sfos = null;
        
         try{
                String cred = user + ":" + password;
                System.out.println("cred[" + cred + "] sambaPath[" + sambaPath + "]");
                System.out.println("fileName[" + fileName + "] fileContent[" + fileContent + "]");
                String filePath = sambaPath + "/" + fileName;
                System.out.println("Target file: " + filePath);
 
                NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(cred);
                SmbFile sFile = new SmbFile(filePath, auth);
                //SmbFile sFile = new SmbFile(filePath);
                sfos = new SmbFileOutputStream(sFile, true);
                sfos.write(fileContent.getBytes());

                successful = true;
                System.out.println("Successful");
            } catch (Exception e) {
                e.printStackTrace();
                err.append(e.toString());
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