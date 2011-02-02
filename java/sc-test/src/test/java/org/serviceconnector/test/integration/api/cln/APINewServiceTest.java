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
package org.serviceconnector.test.integration.api.cln;

import java.security.InvalidParameterException;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.serviceconnector.TestConstants;
import org.serviceconnector.api.cln.SCClient;
import org.serviceconnector.api.cln.SCFileService;
import org.serviceconnector.api.cln.SCPublishService;
import org.serviceconnector.api.cln.SCSessionService;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.test.integration.api.APIIntegrationSuperClientTest;

public class APINewServiceTest extends APIIntegrationSuperClientTest {

	/** The Constant logger. */
	@SuppressWarnings("unused")
	private final static Logger logger = Logger.getLogger(APINewServiceTest.class);
	
	@Before
	public void beforeOneTest() throws Exception {
		super.beforeOneTest();
		client = new SCClient(TestConstants.HOST, TestConstants.PORT_SC_TCP, ConnectionType.NETTY_TCP);
		client.attach();
	}

	/**
	 * Description: create new session service with service name = null<br> 
	 * Expectation:	throws InvalidParameterException
	 */
	@Test(expected = InvalidParameterException.class)
	public void t101_newSessionServiceNameNull() throws Exception {
		client.newSessionService(null);
	}

	/**
	 * Description: create new session service with name = ""<br> 
	 * Expectation:	throws SCMPValidatorException
	 */
	@Test (expected = SCMPValidatorException.class)
	public void t102_newSessionServiceNameEmpty() throws Exception {
		client.newSessionService("");
	}

	/**
	 * Description: create new session service with name = " "<br> 
	 * Expectation:	throws SCMPValidatorException
	 */
	@Test (expected = SCMPValidatorException.class)
	public void t103_newSessionServiceNameBlank() throws Exception {
		client.newSessionService(" ");
	}
	
	/**
	 * Description: create new session service with service name = "The quick brown fox jumps over a lazy dog."<br> 
	 * Expectation:	throws SCMPValidatorException (too long)
	 */
	@Test (expected = SCMPValidatorException.class)
	public void t104_newSessionServiceNameTooLong() throws Exception {
		client.newSessionService(TestConstants.pangram);
	}

	/**
	 * Description: create new session service with service name = "session-1"<br> 
	 * Expectation:	successful creation
	 */
	@Test
	public void t106_newSessionService() throws Exception {
		Assert.assertEquals("create", true, client.newSessionService(TestConstants.sesServiceName1) instanceof SCSessionService);
	}

	/**
	 * Description: create new session service with service name = "session-1" twice<br> 
	 * Expectation:	successful creation
	 */
	@Test
	public void t107_newSessionServiceCalledTwice() throws Exception {
		Assert.assertEquals("create", true, client.newSessionService(TestConstants.sesServiceName1) instanceof SCSessionService);
		Assert.assertEquals("create", true, client.newSessionService(TestConstants.sesServiceName1) instanceof SCSessionService);
	}

	/**
	 * Description: create new session service with service name = "session-1" and "publish-1"<br> 
	 * Expectation:	successful creation
	 */
	@Test
	public void t108_newSessionService() throws Exception {
		Assert.assertEquals("create", true, client.newSessionService(TestConstants.sesServiceName1) instanceof SCSessionService);
		Assert.assertEquals("create", true, client.newSessionService(TestConstants.pubServiceName1) instanceof SCSessionService);
	}
	
	/**
	 * Description: create 1000 new session services with the same name<br> 
	 * Expectation:	successful creation
	 */
	@Test
	public void t109_newSessionService1000times() throws Exception {
		int serviceCount = 1000;
		SCSessionService[] services = new SCSessionService[serviceCount];
		for (int i = 0; i < serviceCount; i++) {
			services[i] = client.newSessionService(TestConstants.sesServiceName1);
			Assert.assertEquals(true, services[i] instanceof SCSessionService);
		}
	}


	
	//-----------------------------------------------------
	
	
	/**
	 * Description: create new publish service with name = null<br> 
	 * Expectation:	throws InvalidParameterException
	 */
	@Test(expected = InvalidParameterException.class)
	public void t201_newPublisServiceNameNull() throws Exception {
		client.newPublishService(null);
	}

	/**
	 * Description: create new publish service with name = ""<br> 
	 * Expectation:	throws SCMPValidatorException
	 */
	@Test (expected = SCMPValidatorException.class)
	public void t202_newPublisServiceNameEmpty() throws Exception {
		client.newPublishService("");
	}

	/**
	 * Description: create new publish service with name = " "<br> 
	 * Expectation:	throws SCMPValidatorException
	 */
	@Test (expected = SCMPValidatorException.class)
	public void t203_newPublisServiceNameBlank() throws Exception {
		client.newPublishService(" ");
	}

	/**
	 * Description: create new publish service with service name = "The quick brown fox jumps over a lazy dog."<br> 
	 * Expectation:	throws SCMPValidatorException. (too long)
	 */
	@Test (expected = SCMPValidatorException.class)
	public void t204_newPublisServiceNameTooLong() throws Exception {
		client.newPublishService(TestConstants.pangram);
	}

	/**
	 * Description: create new publish service with service name = "publish-1"<br> 
	 * Expectation:	successful creation
	 */
	@Test
	public void t206_newPublisService() throws Exception {
		Assert.assertEquals("create", true, client.newPublishService(TestConstants.pubServiceName1) instanceof SCPublishService);
	}

	/**
	 * Description: create new publish service with service name = "publish-1" twice<br> 
	 * Expectation:	successful creation
	 */
	@Test
	public void t207_newPublisServiceCalledTwice() throws Exception {
		Assert.assertEquals("create", true, client.newPublishService(TestConstants.pubServiceName1) instanceof SCPublishService);
		Assert.assertEquals("create", true, client.newPublishService(TestConstants.pubServiceName1) instanceof SCPublishService);
	}

	/**
	 * Description: create new publish service with service name = "session-1" and "publish-1"<br> 
	 * Expectation:	successful creation
	 */
	@Test
	public void t208_newPublisService() throws Exception {
		Assert.assertEquals("create", true, client.newPublishService(TestConstants.sesServiceName1) instanceof SCPublishService);
		Assert.assertEquals("create", true, client.newPublishService(TestConstants.pubServiceName1) instanceof SCPublishService);
	}
	
	/**
	 * Description: create 1000 publish file services with the same name<br> 
	 * Expectation:	successful creation
	 */
	@Test
	public void t209_newPublisService1000times() throws Exception {
		int serviceCount = 1000;
		SCPublishService[] services = new SCPublishService[serviceCount];
		for (int i = 0; i < serviceCount; i++) {
			services[i] = client.newPublishService(TestConstants.pubServiceName1);
			Assert.assertEquals(true, services[i] instanceof SCPublishService);
		}
	}

	//-----------------------------------------------------
	
	
	/**
	 * Description: create new file service with name = null<br> 
	 * Expectation:	throws InvalidParameterException
	 */
	@Test(expected = InvalidParameterException.class)
	public void t301_newFileServiceNameNull() throws Exception {
		client.newFileService(null);
	}

	/**
	 * Description: create new file service with name = ""<br> 
	 * Expectation:	throws SCMPValidatorException
	 */
	@Test (expected = SCMPValidatorException.class)
	public void t302_newFileServiceNameEmpty() throws Exception {
		client.newFileService("");
	}

	/**
	 * Description: create new file service with name = " "<br> 
	 * Expectation:	throws SCMPValidatorException
	 */
	@Test (expected = SCMPValidatorException.class)
	public void t303_newFileServiceNameBlank() throws Exception {
		client.newFileService(" ");
	}

	/**
	 * Description: create new file service with service name = "The quick brown fox jumps over a lazy dog."<br> 
	 * Expectation:	throws SCMPValidatorException (too long)
	 */
	@Test (expected = SCMPValidatorException.class)
	public void t304_newFileServiceNameTooLong() throws Exception {
		client.newFileService(TestConstants.pangram);
	}

	/**
	 * Description: create new file service with service name = "file-1"<br> 
	 * Expectation:	successful creation
	 */
	@Test
	public void t306_newFileService() throws Exception {
		Assert.assertEquals("create", true, client.newFileService(TestConstants.filServiceName1) instanceof SCFileService);
	}

	/**
	 * Description: create new file service with service name = "file-1" twice<br> 
	 * Expectation:	successful creation
	 */
	@Test
	public void t307_newFileServiceCalledTwice() throws Exception {
		Assert.assertEquals("create", true, client.newFileService(TestConstants.filServiceName1) instanceof SCFileService);
		Assert.assertEquals("create", true, client.newFileService(TestConstants.filServiceName1) instanceof SCFileService);
	}

	/**
	 * Description: create new file service with service name = "session-1" and "publish-1"<br> 
	 * Expectation:	successful creation
	 */
	@Test
	public void t308_newFileService() throws Exception {
		Assert.assertEquals("create", true, client.newFileService(TestConstants.sesServiceName1) instanceof SCFileService);
		Assert.assertEquals("create", true, client.newFileService(TestConstants.pubServiceName1) instanceof SCFileService);
	}
	
	/**
	 * Description: create 1000 new file services with the same name<br> 
	 * Expectation:	successful creation
	 */
	@Test
	public void t309_newFileService1000times() throws Exception {
		int serviceCount = 1000;
		SCFileService[] services = new SCFileService[serviceCount];
		for (int i = 0; i < serviceCount; i++) {
			services[i] = client.newFileService(TestConstants.filServiceName1);
			Assert.assertEquals(true, services[i] instanceof SCFileService);
		}
	}
}
