package com.stabilit.sc.app.server.net.ftp;

import java.io.File;

import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.FileSystemFactory;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SCFtpServer {

	public static void main(String[] args) {
		final Logger logger = LoggerFactory.getLogger(SCFtpServer.class);
		
		FtpServer ftpServer = null;
		try {
			FtpServerFactory serverFactory = new FtpServerFactory();
			ListenerFactory listenerFactory = new ListenerFactory();
			// set the port of the listener 
			listenerFactory.setPort(21); 	
			// replace the default listener 
			serverFactory.addListener("default", listenerFactory.createListener());
			PropertiesUserManagerFactory userManagerFactory = new PropertiesUserManagerFactory(); 
			userManagerFactory.setFile(new File("myusers.properties")); 			         
			serverFactory.setUserManager(userManagerFactory.createUserManager());
			ftpServer = serverFactory.createServer(); 
			ftpServer.start();
			synchronized (ftpServer) {
				ftpServer.wait();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ftpServer.stop();
		}

	}
}
