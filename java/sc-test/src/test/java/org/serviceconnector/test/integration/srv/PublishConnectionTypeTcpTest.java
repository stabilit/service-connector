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
package org.serviceconnector.test.integration.srv;

import java.security.InvalidParameterException;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.SCPublishMessage;
import org.serviceconnector.api.srv.SCPublishServer;
import org.serviceconnector.api.srv.SCPublishServerCallback;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.ctrl.util.ProcessesController;
import org.serviceconnector.service.SCServiceException;
import org.serviceconnetor.TestConstants;

public class PublishConnectionTypeTcpTest {
	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(PublishConnectionTypeTcpTest.class);

	private SCPublishServer server;
	private Exception ex;

	private static Process scProcess;
	private static ProcessesController ctrl;

	@BeforeClass
	public static void oneTimeSetUp() throws Exception {
		ctrl = new ProcessesController();
		try {
			scProcess = ctrl.startSC(TestConstants.log4jSCProperties, TestConstants.SCProperties);
		} catch (Exception e) {
			logger.error("oneTimeSetUp", e);
		}
	}

	@AfterClass
	public static void oneTimeTearDown() throws Exception {
		ctrl.stopProcess(scProcess, TestConstants.log4jSCProperties);
		ctrl = null;
		scProcess = null;
	}

	@Before
	public void setUp() throws Exception {
		server = new SCPublishServer();
		server.startListener(TestConstants.HOST, TestConstants.PORT_LISTENER, 0);
		server.registerServer(TestConstants.HOST, TestConstants.PORT_TCP, TestConstants.publishServiceName, 1, 1,
				new SCPublishServerCallback());
	}

	@After
	public void tearDown() throws Exception {
		server.deregisterServer(TestConstants.publishServiceName);
		server.destroy();
		server = null;
		ex = null;
	}

	@Test(expected = SCMPValidatorException.class)
	public void publish_serviceNameNull_throwsValidatorException() throws Exception {
		SCPublishMessage publishMessage = new SCPublishMessage();
		publishMessage.setMask(TestConstants.mask);
		publishMessage.setData("something");
		server.publish(null, publishMessage);
	}

	@Test(expected = SCMPValidatorException.class)
	public void publish_serviceNameEmpty_throwsValidatorException() throws Exception {
		SCPublishMessage publishMessage = new SCPublishMessage();
		publishMessage.setMask(TestConstants.mask);
		publishMessage.setData("something");
		server.publish("", publishMessage);
	}

	@Test(expected = SCServiceException.class)
	public void publish_serviceNameWhiteSpace_throwsSCException() throws Exception {
		SCPublishMessage publishMessage = new SCPublishMessage();
		publishMessage.setMask(TestConstants.mask);
		publishMessage.setData("something");
		server.publish(" ", publishMessage);
	}

	@Test(expected = SCServiceException.class)
	public void publish_serviceNameOneChar_throwsSCException() throws Exception {
		SCPublishMessage publishMessage = new SCPublishMessage();
		publishMessage.setMask(TestConstants.mask);
		publishMessage.setData("something");
		server.publish("a", publishMessage);
	}

	@Test(expected = SCServiceException.class)
	public void publish_serviceNameNotExistingService_throwsSCException() throws Exception {
		SCPublishMessage publishMessage = new SCPublishMessage();
		publishMessage.setMask(TestConstants.mask);
		publishMessage.setData("something");
		server.publish("notExistingService", publishMessage);
	}

	@Test(expected = SCServiceException.class)
	public void publish_serviceName32LongString_throwsSCException() throws Exception {
		SCPublishMessage publishMessage = new SCPublishMessage();
		publishMessage.setMask(TestConstants.mask);
		publishMessage.setData("something");
		server.publish(TestConstants.stringLength32, publishMessage);
	}

	@Test(expected = SCMPValidatorException.class)
	public void publish_serviceName33LongString_throwsValidatorException() throws Exception {
		SCPublishMessage publishMessage = new SCPublishMessage();
		publishMessage.setMask(TestConstants.mask);
		publishMessage.setData("something");
		server.publish(TestConstants.stringLength33, publishMessage);
	}

	@Test(expected = SCServiceException.class)
	public void publish_serviceNameDisabled_throwsSCException() throws Exception {
		SCPublishMessage publishMessage = new SCPublishMessage();
		publishMessage.setMask(TestConstants.mask);
		publishMessage.setData("something");
		server.publish(TestConstants.publishServiceName, publishMessage);
	}

	@Test
	public void publish_serviceNameValid_passes() throws Exception {
		SCPublishMessage publishMessage = new SCPublishMessage();
		publishMessage.setMask(TestConstants.mask);
		publishMessage.setData("something");
		server.publish(TestConstants.publishServiceName, publishMessage);
	}

	@Test(expected = SCServiceException.class)
	public void publish_serviceNameSessionServiceWithoutRegistering_throwsSCException() throws Exception {
		SCPublishMessage publishMessage = new SCPublishMessage();
		publishMessage.setMask(TestConstants.mask);
		publishMessage.setData("something");
		server.publish(TestConstants.sessionServiceName, publishMessage);
	}

	@Test(expected = SCServiceException.class)
	public void publish_serviceNameSessionServiceWithRegistering_throwsSCException() throws Exception {
		server.registerServer(TestConstants.HOST, TestConstants.PORT_TCP, TestConstants.sessionServiceName, 1, 1,
				new SCPublishServerCallback());
		try {
			SCPublishMessage publishMessage = new SCPublishMessage();
			publishMessage.setMask(TestConstants.mask);
			publishMessage.setData("something");
			server.publish(TestConstants.sessionServiceName, publishMessage);
		} catch (Exception e) {
			ex = e;
		} finally {
			server.deregisterServer(TestConstants.sessionServiceName);
		}
		if (ex != null) {
			throw ex;
		}
	}

	@Test(expected = SCMPValidatorException.class)
	public void publish_maskNull_throwsValidatorException() throws Exception {
		SCPublishMessage publishMessage = new SCPublishMessage();
		publishMessage.setMask(null);
		publishMessage.setData("something");
		server.publish(TestConstants.publishServiceName, publishMessage);
	}

	@Test(expected = SCMPValidatorException.class)
	public void publish_maskEmpty_throwsValidatorException() throws Exception {
		SCPublishMessage publishMessage = new SCPublishMessage();
		publishMessage.setMask("");
		publishMessage.setData("something");
		server.publish(TestConstants.publishServiceName, publishMessage);
	}

	@Test
	public void publish_maskWhiteSpace_passes() throws Exception {
		SCPublishMessage publishMessage = new SCPublishMessage();
		publishMessage.setMask(" ");
		publishMessage.setData("something");
		server.publish(TestConstants.publishServiceName, publishMessage);
	}

	@Test
	public void publish_maskOneChar_passes() throws Exception {
		SCPublishMessage publishMessage = new SCPublishMessage();
		publishMessage.setMask("a");
		publishMessage.setData("something");
		server.publish(TestConstants.publishServiceName, publishMessage);
	}

	@Test
	public void publish_maskPangram_passes() throws Exception {
		SCPublishMessage publishMessage = new SCPublishMessage();
		publishMessage.setMask(TestConstants.pangram);
		publishMessage.setData("something");
		server.publish(TestConstants.publishServiceName, publishMessage);
	}

	@Test
	public void publish_mask256LongString_passes() throws Exception {
		SCPublishMessage publishMessage = new SCPublishMessage();
		publishMessage.setMask(TestConstants.stringLength256);
		publishMessage.setData("something");
		server.publish(TestConstants.publishServiceName, publishMessage);
	}

	@Test(expected = SCMPValidatorException.class)
	public void publish_mask257LongString_throwsValidatorException() throws Exception {
		SCPublishMessage publishMessage = new SCPublishMessage();
		publishMessage.setMask(TestConstants.stringLength257);
		publishMessage.setData("something");
		server.publish(TestConstants.publishServiceName, publishMessage);
	}

	@Test
	public void publish_maskValidWithoutPercentSign_passes() throws Exception {
		SCPublishMessage publishMessage = new SCPublishMessage();
		publishMessage.setMask(TestConstants.mask);
		publishMessage.setData("something");
		server.publish(TestConstants.publishServiceName, publishMessage);
	}

	@Test
	public void publish_maskValidWithPercentSign_passes() throws Exception {
		SCPublishMessage publishMessage = new SCPublishMessage();
		publishMessage.setMask("0000121%%%%%%%%%%%%%%%-----------X-----------");
		publishMessage.setData("something");
		server.publish(TestConstants.publishServiceName, publishMessage);
	}

	@Test
	public void publish_dataNull_passes() throws Exception {
		SCPublishMessage publishMessage = new SCPublishMessage();
		publishMessage.setMask(TestConstants.mask);
		publishMessage.setData(null);
		server.publish(TestConstants.publishServiceName, publishMessage);
	}

	@Test
	public void publish_dataEmpty_passes() throws Exception {
		SCPublishMessage publishMessage = new SCPublishMessage();
		publishMessage.setMask(TestConstants.mask);
		publishMessage.setData("");
		server.publish(TestConstants.publishServiceName, publishMessage);
	}

	@Test
	public void publish_dataWhiteSpace_passes() throws Exception {
		SCPublishMessage publishMessage = new SCPublishMessage();
		publishMessage.setMask(TestConstants.mask);
		publishMessage.setData(" ");
		server.publish(TestConstants.publishServiceName, publishMessage);
	}

	@Test
	public void publish_dataOneChar_passes() throws Exception {
		SCPublishMessage publishMessage = new SCPublishMessage();
		publishMessage.setMask(TestConstants.mask);
		publishMessage.setData("a");
		server.publish(TestConstants.publishServiceName, publishMessage);
	}

	@Test
	public void publish_dataPangram_passes() throws Exception {
		SCPublishMessage publishMessage = new SCPublishMessage();
		publishMessage.setMask(TestConstants.mask);
		publishMessage.setData(TestConstants.pangram);
		server.publish(TestConstants.publishServiceName, publishMessage);
	}

	@Test
	public void publish_data1000LongString_passes() throws Exception {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 1000; i++) {
			sb.append("a");
		}
		SCPublishMessage publishMessage = new SCPublishMessage();
		publishMessage.setMask(TestConstants.mask);
		publishMessage.setData(sb.toString());
		server.publish(TestConstants.publishServiceName, publishMessage);
	}

	@Test
	public void publish_data10000LongString_passes() throws Exception {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 10000; i++) {
			sb.append("a");
		}
		SCPublishMessage publishMessage = new SCPublishMessage();
		publishMessage.setMask(TestConstants.mask);
		publishMessage.setData(sb.toString());
		server.publish(TestConstants.publishServiceName, publishMessage);
	}

	@Test(expected = InvalidParameterException.class)
	public void publish_dataEmptyObject_passes() throws Exception {
		SCPublishMessage publishMessage = new SCPublishMessage();
		publishMessage.setMask(TestConstants.mask);
		publishMessage.setData(new Object());
		server.publish(TestConstants.publishServiceName, publishMessage);
	}

	@Test(expected = InvalidParameterException.class)
	public void publish_dataSCMessage_passes() throws Exception {
		SCPublishMessage publishMessage = new SCPublishMessage();
		publishMessage.setMask(TestConstants.mask);
		publishMessage.setData(new SCMessage());
		server.publish(TestConstants.publishServiceName, publishMessage);
	}

	@Test
	public void publish_data60kBArray_passes() throws Exception {
		SCPublishMessage publishMessage = new SCPublishMessage();
		publishMessage.setMask(TestConstants.mask);
		publishMessage.setData(new byte[TestConstants.dataLength60kB]);
		server.publish(TestConstants.publishServiceName, publishMessage);
	}

	@Test
	public void publish_data1MBArray_passes() throws Exception {
		SCPublishMessage publishMessage = new SCPublishMessage();
		publishMessage.setMask(TestConstants.mask);
		publishMessage.setData(new byte[TestConstants.dataLength1MB]);
		server.publish(TestConstants.publishServiceName, publishMessage);
	}

	@Test
	public void publish_10000Messages_passes() throws Exception {
		for (int i = 0; i < 10000; i++) {
			SCPublishMessage publishMessage = new SCPublishMessage();
			publishMessage.setMask(TestConstants.mask);
			publishMessage.setData(new byte[128]);
			server.publish(TestConstants.publishServiceName, publishMessage);
		}
	}
}
