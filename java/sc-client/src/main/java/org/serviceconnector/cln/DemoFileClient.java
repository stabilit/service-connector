/*
 *       Copyright © 2010 STABILIT Informatik AG, Switzerland                  *
 *                                                                             *
 *  Licensed under the Apache License, Version 2.0 (the "License");            *
 *  you may not use this file except in compliance with the License.           *
 *  You may obtain a copy of the License at                                    *
 *                                                                             *
 *  http://www.apache.org/licenses/LICENSE-2.0                                 *
 *                                                                             *
 *  Unless required by applicable law or agreed to in writing, software        *
 *  distributed under the License is distributed on an "AS IS" BASIS,          *
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 *  See the License for the specific language governing permissions and        *
 *  limitations under the License.                                             *
 */
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
			sc.setMaxConnections(20);								// can be set before attach
			sc.setKeepaliveIntervalInSeconds(10);					// can be set before attach
			sc.attach();											// regular
			sc.attach(10);											// alternative with operation timeout
		
			SCFileService service = sc.newFileService("file-service");	// no other params possible

			localFile = new File("src/main/resources/ClientContent.zip");
			inpStream = new FileInputStream(localFile);
			targetFileName = "uploadedContent.txt";
						
			service.uploadFile(targetFileName, inpStream);			// regular
			service.uploadFile(targetFileName, inpStream, 600);		// alternative with operation timeout

			localFile = new File("src/main/resources/ServerContent.zip");
			FileOutputStream outStream = new FileOutputStream(new File("localFile"));
			
			service.downloadFile(targetFileName, outStream);		// regular
			service.downloadFile(targetFileName, outStream, 600);	// alternative with operation timeout
			outStream.close();
						
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
