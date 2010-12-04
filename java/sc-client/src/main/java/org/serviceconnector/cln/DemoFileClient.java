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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.apache.log4j.Logger;
import org.serviceconnector.api.cln.SCClient;
import org.serviceconnector.api.cln.SCFileService;
import org.serviceconnector.net.ConnectionType;

public class DemoFileClient extends Thread {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(DemoFileClient.class);

	public static void main(String[] args) {
		DemoFileClient demoFileClient = new DemoFileClient();
		demoFileClient.start();
	}

	@Override
	public void run() {

		//SCClient sc = new SCClient("localhost", 7000); // regular defaults must be documented in javadoc
		SCClient sc = new SCClient("localhost", 7000, ConnectionType.NETTY_HTTP); // alternative with connection type

		try {
			sc.setMaxConnections(20); // can be set before attach
			sc.setKeepAliveIntervalSeconds(10); // can be set before attach
			sc.attach(); // regular
//			sc.attach(10); // alternative with operation timeout

			SCFileService service = sc.newFileService("file-1"); // no other params possible

			File localFile = new File("src/main/resources/ClientContent.txt");
			InputStream inpStream = new FileInputStream(localFile);
			String targetFileName = "uploadedContent.txt";

			service.uploadFile(targetFileName, inpStream); // regular
			// service.uploadFile(600, targetFileName, inpStream); // alternative with operation timeout

			localFile = new File("src/main/resources/ServerContent.txt");
			FileOutputStream outStream = new FileOutputStream(localFile);
			targetFileName = "ClientContentCopied.txt";
			service.downloadFile(targetFileName, outStream); // regular
			// service.downloadFile(600, targetFileName, outStream); // alternative with operation timeout
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
}