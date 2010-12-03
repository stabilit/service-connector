/*-----------------------------------------------------------------------------*
 *                                                                             *
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
 *-----------------------------------------------------------------------------*/
package org.serviceconnector.test.sc.cln.api;

import java.io.File;
import java.io.FileOutputStream;

import org.junit.Before;
import org.junit.Test;
import org.serviceconnector.TestConstants;
import org.serviceconnector.api.cln.SCClient;
import org.serviceconnector.api.cln.SCFileService;
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.test.sc.SetupTestCases;

public class ClnAPIFileTestCase {

	@Before
	public void beforeOneTest() {
		SetupTestCases.setupSC();
	}

	@Test
	public void testClnAPI() throws Exception {

		SCClient sc = null;
		try {
			sc = new SCClient(TestConstants.HOST, TestConstants.PORT_HTTP,ConnectionType.NETTY_HTTP);
			sc.attach();

			SCFileService fileServiceA = sc.newFileService("file-1");

			String targetFileName = "uploadFile.txt";

//			File uploadFile = new File("target/classes/uploadFile.txt");
//			FileInputStream fileStream = new FileInputStream(uploadFile);
//			fileServiceA.uploadFile(targetFileName, fileStream, 600);

//			targetFileName = "uploadFileLarge.zip";
//
//			uploadFile = new File("target/classes/uploadFileLarge.zip");
//			fileStream = new FileInputStream(uploadFile);
//			fileServiceA.uploadFile(targetFileName, fileStream, 600);

			targetFileName = "uploadFile.txt";
			FileOutputStream outStream = new FileOutputStream(new File("src/main/resources/downloaded_uploadFile.txt"));
			fileServiceA.downloadFile(600, targetFileName, outStream);
			outStream.close();

//			targetFileName = "uploadFileLarge.zip";
//			outStream = new FileOutputStream(new File("src/main/resources/downloaded_uploadFileLarge.zip"));
//			fileServiceA.downloadFile(targetFileName, outStream, 600);
//			outStream.close();
			// String sourceFileName = "";
			// OutputStream outStream = null;
			// sc.downloadFile("serviceName", sourceFileName, outStream);
			// List<String> fileNames = sc.listFiles("serviceName");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				sc.detach();
			} catch (Exception e) {
				sc = null;
			}
		}
	}
}
