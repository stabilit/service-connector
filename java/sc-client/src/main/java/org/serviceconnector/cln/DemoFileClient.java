package org.serviceconnector.cln;

import org.apache.log4j.Logger;

public class DemoFileClient extends Thread {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(DemoFileClient.class);
	
	public static void main(String[] args) {
		DemoFileClient demoFileClient = new DemoFileClient();
		demoFileClient.start();
	}

	/*
	@Override
	public void run() {
	
		SCClient sc = new SCClient("localhost", 7000);				// regular defaults must be documented in javadoc
		SCClient sc = new SCClient("localhost", 7000, ConnectionType.NETTY-HTTP);	// alternative with connection type
		
		try {
			sc.setConnectionType(ConnectionType.NETTY-HTTP);		// can be set before attach
			sc.setHost("localhost");								// can be set before attach
			sc.setPort(7000);										// can be set before attach
			sc.setMaxConnections(20);								// can be set before attach
			sc.setKeepaliveIntervalInSeconds(10);					// can be set before attach
			sc.attach();											// regular
			sc.attach(10);											// alternative with operation timeout
		
			SCFileService service = sc.newFileService("P01_logging");		// no other params possible

			uploadFile = new File("target/classes/uploadFileLarge.zip");
			fileStream = new FileInputStream(uploadFile);
			service.uploadFile(targetFileName, fileStream, 600);

			targetFileName = "uploadFile.txt";
			FileOutputStream outStream = new FileOutputStream(new File("src/main/resources/downloaded_uploadFile.txt"));
			service.downloadFile(targetFileName, outStream);		// regular
			service.downloadFile(targetFileName, outStream, 600);	// alternative with operation timeout
			outStream.close();
			
			SCSession session = service.createSession();			//regular
			SCSession session = service.createSession(10);			//alternative with operation timeout 
			SCMessage msg = new SCMessage();
			msg.setSessionInfo("sessionInfo");						// optional
			msg.setData("certificate or what so ever");				// optional
			SCSession session = service.createSession(10, msg);		//alternative with operation timeout and message 
			
		} catch (Exception e) {
			logger.error("run", e);
		} finally {
			try {
				sc.detach();
			} catch (Exception e) {
				logger.error("cleanup", e);
			}
		}
	}
	*/
	
}
