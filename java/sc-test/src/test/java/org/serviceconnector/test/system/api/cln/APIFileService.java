/*
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
 */
package org.serviceconnector.test.system.api.cln;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.serviceconnector.TestConstants;
import org.serviceconnector.api.cln.SCFileService;
import org.serviceconnector.api.cln.SCMgmtClient;
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.test.system.api.APISystemSuperSessionClientTest;
import org.serviceconnector.util.FileUtility;

public class APIFileService extends APISystemSuperSessionClientTest {

	protected SCMgmtClient client;

	@Before
	public void beforeOneTest() throws Exception {
		super.beforeOneTest();
		client = new SCMgmtClient(TestConstants.HOST, TestConstants.PORT_SC_TCP, ConnectionType.NETTY_TCP);
		client.attach();
	}
	
	@After
	public void afterOneTest() throws Exception {
		try {
			if (client != null) {
			   client.detach();
			}
		} catch (Exception e) {
		}
		client = null;
		super.afterOneTest();
	}

	/**
	 * Description: upload file via file-2 service<br>
	 * Expectation: passes
	 */
	@Test
	public void t01_upload() throws Exception {
		SCFileService service = client.newFileService(TestConstants.filServiceName2);
		String localFile = "uploadFile.txt";
		String localpath = "src/main/resources/";
		String remoteFileName = "uploadedFile.txt";
		File inputFile = new File(localpath + localFile);
		InputStream inpStream = new FileInputStream(inputFile);
		service.uploadFile(remoteFileName, inpStream);
		inpStream.close();
		FileUtility.waitExists(TestConstants.filServiceLocation2+remoteFileName, 0);
		FileUtility.deleteFile(TestConstants.filServiceLocation2+remoteFileName);		
	}

	/**
	 * Description: list file uploaded via file-2 service<br>
	 * Expectation: passes
	 */
	@Test
	public void t02_list() throws Exception {
		SCFileService service = client.newFileService(TestConstants.filServiceName2);
		// upload file first
		String localFile = "uploadFile.txt";
		String localpath = "src/main/resources/";
		String remoteFileName = "uploadedFile.txt";
		File inputFile = new File(localpath + localFile);
		InputStream inpStream = new FileInputStream(inputFile);
		service.uploadFile(remoteFileName, inpStream);
		inpStream.close();
		
		//list now
		List<String> fileNameList = service.listFiles();
		for (String fileName : fileNameList) {
			testLogger.info(fileName);
		}
		Assert.assertEquals("list is empty", 1, fileNameList.size());
		Assert.assertEquals("filename is not equal", remoteFileName, fileNameList.get(0) );
		FileUtility.deleteFile(TestConstants.filServiceLocation2+remoteFileName);
	}

	/**
	 * Description: download file uploaded via file-2 service<br>
	 * Expectation: passes
	 */
	@Test
	public void t03_download() throws Exception {
		SCFileService service = client.newFileService(TestConstants.filServiceName2);
		// upload file first
		String localFile = "uploadFile.txt";
		String localpath = "src/main/resources/";
		String remoteFileName = "uploadedFile.txt";
		File inputFile = new File(localpath + localFile);
		InputStream inpStream = new FileInputStream(inputFile);
		service.uploadFile(remoteFileName, inpStream);
		inpStream.close();
		FileUtility.waitExists(TestConstants.filServiceLocation2+remoteFileName, 0);
		
		//download now
		localFile = "downloadContent.txt";
		localpath = "src/main/resources/";
		remoteFileName = "uploadedFile.txt";
		File outputFile = new File(localpath + localFile);
		FileOutputStream outStream = new FileOutputStream(outputFile);
		service.downloadFile(remoteFileName, outStream); // regular download
		outStream.close(
				);
		FileUtility.waitExists(localpath + localFile, 0);
		FileUtility.deleteFile(localpath + localFile);	
		FileUtility.deleteFile(TestConstants.filServiceLocation2+remoteFileName);	
	}

}