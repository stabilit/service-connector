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
package org.serviceconnector.test.sc.register;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;
import org.serviceconnector.TestConstants;
import org.serviceconnector.TestUtil;
import org.serviceconnector.call.SCMPCallFactory;
import org.serviceconnector.call.SCMPDeRegisterServerCall;
import org.serviceconnector.call.SCMPInspectCall;
import org.serviceconnector.call.SCMPRegisterServerCall;
import org.serviceconnector.conf.CommunicatorConfig;
import org.serviceconnector.net.req.IRequester;
import org.serviceconnector.net.req.RequesterContext;
import org.serviceconnector.net.req.SCRequester;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPMessageFault;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMsgType;
import org.serviceconnector.test.sc.SCTest;
import org.serviceconnector.test.sc.SuperTestCase;
import org.serviceconnector.test.sc.TestContext;
import org.serviceconnector.util.SynchronousCallback;

public class RegisterServerTestCase extends SuperTestCase {

	protected TestRegisterServerCallback registerCallback;

	/**
	 * The Constructor.
	 * 
	 * @param fileName
	 *            the file name
	 */
	public RegisterServerTestCase(String fileName) {
		super(fileName);
		this.registerCallback = new TestRegisterServerCallback();
	}

	@Test
	public void failRegisterServerForUnknownService() throws Exception {
		SCMPRegisterServerCall registerServerCall = (SCMPRegisterServerCall) SCMPCallFactory.REGISTER_SERVER_CALL.newInstance(req,
				"notConfiguredServiceName");

		registerServerCall.setMaxSessions(10);
		registerServerCall.setMaxConnections(10);
		registerServerCall.setPortNumber(TestConstants.PORT_LISTENER);
		registerServerCall.setImmediateConnect(true);
		registerServerCall.setKeepAliveInterval(360);

		registerServerCall.invoke(this.registerCallback, 1000);
		SCMPMessageFault fault = (SCMPMessageFault) this.registerCallback.getMessageSync(3000);
		Assert.assertTrue(fault.isFault());
		TestUtil.verifyError((SCMPMessageFault) fault, SCMPError.NOT_FOUND, SCMPMsgType.REGISTER_SERVER);
	}

	@Test
	public void failRegisterServerCallWrongHeader() throws Exception {
		SCMPRegisterServerCall registerServerCall = (SCMPRegisterServerCall) SCMPCallFactory.REGISTER_SERVER_CALL.newInstance(req,
				"session-1");

		// keep alive interval not set
		registerServerCall.setMaxSessions(10);
		registerServerCall.setMaxConnections(10);
		registerServerCall.setPortNumber(9100);
		registerServerCall.setImmediateConnect(true);
		registerServerCall.invoke(this.registerCallback, 1000);
		SCMPMessage fault = this.registerCallback.getMessageSync(3000);
		Assert.assertTrue(fault.isFault());
		TestUtil.verifyError((SCMPMessageFault) fault, SCMPError.HV_WRONG_KEEPALIVE_INTERVAL, SCMPMsgType.REGISTER_SERVER);

		// maxSessions 0 value
		registerServerCall.setPortNumber(9100);
		registerServerCall.setMaxSessions(0);
		registerServerCall.setMaxConnections(10);
		registerServerCall.setImmediateConnect(true);
		registerServerCall.setKeepAliveInterval(360);
		registerServerCall.invoke(this.registerCallback, 1000);
		fault = this.registerCallback.getMessageSync(3000);
		Assert.assertTrue(fault.isFault());
		TestUtil.verifyError((SCMPMessageFault) fault, SCMPError.HV_WRONG_MAX_SESSIONS, SCMPMsgType.REGISTER_SERVER);

		// maxConnections 0 value
		registerServerCall.setPortNumber(9100);
		registerServerCall.setMaxSessions(10);
		registerServerCall.setMaxConnections(0);
		registerServerCall.setImmediateConnect(true);
		registerServerCall.setKeepAliveInterval(360);
		registerServerCall.invoke(this.registerCallback, 1000);
		fault = this.registerCallback.getMessageSync(3000);
		Assert.assertTrue(fault.isFault());
		TestUtil.verifyError((SCMPMessageFault) fault, SCMPError.HV_WRONG_MAX_CONNECTIONS, SCMPMsgType.REGISTER_SERVER);

		// port too high 10000
		registerServerCall.setMaxSessions(10);
		registerServerCall.setMaxConnections(10);
		registerServerCall.setPortNumber(910000);
		registerServerCall.setImmediateConnect(true);
		registerServerCall.setKeepAliveInterval(360);
		registerServerCall.invoke(this.registerCallback, 1000);
		fault = this.registerCallback.getMessageSync(3000);
		Assert.assertTrue(fault.isFault());
		TestUtil.verifyError((SCMPMessageFault) fault, SCMPError.HV_WRONG_PORTNR, SCMPMsgType.REGISTER_SERVER);
	}

	@Test
	public void registerServerCall() throws Exception {
		List<String> hosts = new ArrayList<String>();
		hosts.add(TestConstants.HOST);
		CommunicatorConfig config = new CommunicatorConfig("RegisterServerCallTester", hosts, TestConstants.PORT_TCP, "netty.tcp",
				1000, 60, 10);
		RequesterContext context = new TestContext(config);
		IRequester req = new SCRequester(context);

		SCMPRegisterServerCall registerServerCall = (SCMPRegisterServerCall) SCMPCallFactory.REGISTER_SERVER_CALL.newInstance(req,
				"publish-1");

		registerServerCall.setMaxSessions(10);
		registerServerCall.setMaxConnections(10);
		registerServerCall.setPortNumber(51000);
		registerServerCall.setImmediateConnect(true);
		registerServerCall.setKeepAliveInterval(360);

		registerServerCall.invoke(this.registerCallback, 2000);
		TestUtil.checkReply(this.registerCallback.getMessageSync(3000));
		/*************** scmp inspect ********/
		SCMPInspectCall inspectCall = (SCMPInspectCall) SCMPCallFactory.INSPECT_CALL.newInstance(req);
		inspectCall.invoke(this.registerCallback, 3000);
		SCMPMessage inspect = this.registerCallback.getMessageSync(3000);

		/*********************************** Verify registry entries in SC ********************************/
		String inspectMsg = (String) inspect.getBody();
		Map<String, String> inspectMap = SCTest.convertInspectStringToMap(inspectMsg);

		// TODO this will not work if configuration is changed
		String expectedScEntry = "sc2-session-2:0|sc1-session-1:0|session-1:0 - session-1_localhost/:30000 : 10 - session-1_localhost/:41000 : 10 - session-1_localhost/:42000 : 1|sc1-publish-1:0|publish-2:0|sc2-publish-2:0|file-1:file-1:ENABLED:file|session-2:0|publish-1:0 - publish-1_localhost/:51000 : 1 - publish-1_localhost/:51000 : 10|";
		String scEntry = inspectMap.get("serviceRegistry");
		//SCTest.Assert.assertEqualsUnorderedStringIgnorePorts(expectedScEntry, scEntry);
		// TODO this will not work if configuration is changed
		expectedScEntry = "session-1_localhost/:session-1_localhost/:42000 : 1|session-1_localhost/:session-1_localhost/:41000 : 10|session-1_localhost/:session-1_localhost/:30000 : 10|publish-1_localhost/:publish-1_localhost/:51000 : 1|fileServer:fileServer:80|publish-1_localhost/:publish-1_localhost/:51000 : 10|";
		scEntry = (String) inspectMap.get("serverRegistry");
//		SCTest.Assert.assertEqualsUnorderedStringIgnorePorts(expectedScEntry, scEntry);

		SCMPDeRegisterServerCall deRegisterServerCall = (SCMPDeRegisterServerCall) SCMPCallFactory.DEREGISTER_SERVER_CALL
				.newInstance(req, "publish-1");
		deRegisterServerCall.invoke(this.registerCallback, 1000);
		TestUtil.checkReply(this.registerCallback.getMessageSync(3000));

		/*********************************** Verify registry entries in SC ********************************/
		inspectCall = (SCMPInspectCall) SCMPCallFactory.INSPECT_CALL.newInstance(req);
		inspectCall.invoke(this.registerCallback, 1000);
		inspect = this.registerCallback.getMessageSync(3000);
		inspectMsg = (String) inspect.getBody();
		inspectMap = SCTest.convertInspectStringToMap(inspectMsg);
		expectedScEntry = "session-1_localhost/:session-1_localhost/:30000 : 10|publish-1_localhost/:publish-1_localhost/:51000 : 1|fileServer:fileServer:80|";
		scEntry = (String) inspectMap.get("serverRegistry");
//		SCTest.Assert.assertEqualsUnorderedStringIgnorePorts(expectedScEntry, scEntry);
	}

	protected class TestRegisterServerCallback extends SynchronousCallback {
	}
}
