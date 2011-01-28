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
import java.util.List;

import org.apache.log4j.Logger;
import org.serviceconnector.api.cln.SCClient;
import org.serviceconnector.api.cln.SCFileService;
import org.serviceconnector.net.ConnectionType;

public class DemoFileClient extends Thread {

	/** The Constant logger. */
	private final static Logger logger = Logger.getLogger(DemoFileClient.class);

	public static void main(String[] args) {
		DemoFileClient demoFileClient = new DemoFileClient();
		demoFileClient.start();
	}

	@Override
	public void run() {
		// Connection to SC over HTTP
		SCClient sc = new SCClient("localhost", 7000, ConnectionType.NETTY_HTTP);

		try {
			sc.setMaxConnections(20); // can be set before attach, default 100 Connections
			sc.setKeepAliveIntervalSeconds(10); // can be set before attach, default 0 -> inactive
			sc.attach(); // attaching client to SC , communication starts

			SCFileService service = sc.newFileService("file-1"); // name of the service to use

			List<String> fileNameList = service.listFiles();
			for (String fileName : fileNameList) {
				System.out.println(fileName);
			}
			
			File localFile = new File("src/main/resources/uploadContent.txt");
			InputStream inpStream = new FileInputStream(localFile);
			String targetFileName = "uploadContent.txt";

			service.uploadFile(targetFileName, inpStream); // regular upload

			localFile = new File("src/main/resources/downloadContent.txt");
			FileOutputStream outStream = new FileOutputStream(localFile);
			targetFileName = "uploadContent.txt";
			service.downloadFile(targetFileName, outStream); // regular download
			outStream.close();
		} catch (Exception e) {
			logger.error("run", e);
		} finally {
			try {
				sc.detach(); // detaches from SC, stops communication
			} catch (Exception e) {
				logger.error("cleanup", e);
			}
		}
	}
}