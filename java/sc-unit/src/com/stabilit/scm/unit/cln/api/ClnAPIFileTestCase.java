/*-----------------------------------------------------------------------------*
 *                                                                             *
 *       Copyright � 2010 STABILIT Informatik AG, Switzerland                  *
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
package com.stabilit.scm.unit.cln.api;

import java.io.InputStream;
import java.io.OutputStream;

import org.junit.Before;
import org.junit.Test;

import com.stabilit.scm.cln.service.IClientServiceConnector;
import com.stabilit.scm.common.service.ServiceConnectorFactory;
import com.stabilit.scm.unit.test.SetupTestCases;

public class ClnAPIFileTestCase {

	@Before
	public void setUp() {
		SetupTestCases.setupAll();
	}

	@Test
	public void testClnAPI() throws Exception {

		IClientServiceConnector sc = null;
		try {
			sc = ServiceConnectorFactory.newClientInstance("localhost", 8080);
			sc.connect();	// connects to SC, starts observing connection

			/* TODO (trn) Bitte umbauen auf:
			
			ISCService fileServiceA = sc.newFileService("logs");
			
			String targetFileName = "";
			InputStream inStream = null;
			fileServiceA.uploadFile(targetFileName, inStream);
			
			String sourceFileName = "";
			OutputStream outStream = null;
			fileServiceA.downloadFile(sourceFileName, outStream);
			*/
			
			String targetFileName = "";
			InputStream inStream = null;
			sc.uploadFile("serviceName", targetFileName, inStream);

			String sourceFileName = "";
			OutputStream outStream = null;
			sc.downloadFile("serviceName", sourceFileName, outStream);
			// List<String> fileNames = sc.listFiles("serviceName");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				// disconnects from SC
				sc.disconnect();
			} catch (Exception e) {
				sc = null;
			}
		}
	}
}
