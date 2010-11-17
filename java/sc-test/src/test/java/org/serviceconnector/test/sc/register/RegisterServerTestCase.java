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

import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;
import org.serviceconnector.call.SCMPCallFactory;
import org.serviceconnector.call.SCMPDeRegisterServerCall;
import org.serviceconnector.call.SCMPInspectCall;
import org.serviceconnector.call.SCMPRegisterServerCall;
import org.serviceconnector.conf.CommunicatorConfig;
import org.serviceconnector.ctrl.util.TestConstants;
import org.serviceconnector.net.req.IRequester;
import org.serviceconnector.net.req.RequesterContext;
import org.serviceconnector.net.req.SCRequester;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPFault;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMsgType;
import org.serviceconnector.test.sc.SCTest;
import org.serviceconnector.test.sc.SuperTestCase;
import org.serviceconnector.test.sc.connectionPool.TestContext;
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
		SCMPFault fault = (SCMPFault) this.registerCallback.getMessageSync();
		Assert.assertTrue(fault.isFault());
		SCTest.verifyError((SCMPFault) fault, SCMPError.NOT_FOUND, " [service not found]", SCMPMsgType.REGISTER_SERVER);
	}

	@Test
	public void failRegisterServerCallWrongHeader() throws Exception {
		SCMPRegisterServerCall registerServerCall = (SCMPRegisterServerCall) SCMPCallFactory.REGISTER_SERVER_CALL.newInstance(req,
				"local-session-service");

		// keep alive interval not set
		registerServerCall.setMaxSessions(10);
		registerServerCall.setMaxConnections(10);
		registerServerCall.setPortNumber(9100);
		registerServerCall.setImmediateConnect(true);
		registerServerCall.invoke(this.registerCallback, 1000);
		SCMPMessage fault = this.registerCallback.getMessageSync();
		Assert.assertTrue(fault.isFault());
		SCTest.verifyError((SCMPFault) fault, SCMPError.HV_WRONG_KEEPALIVE_INTERVAL, " [IntValue must be set]",
				SCMPMsgType.REGISTER_SERVER);

		// maxSessions 0 value
		registerServerCall.setPortNumber(9100);
		registerServerCall.setMaxSessions(0);
		registerServerCall.setMaxConnections(10);
		registerServerCall.setImmediateConnect(true);
		registerServerCall.setKeepAliveInterval(360);
		registerServerCall.invoke(this.registerCallback, 1000);
		fault = this.registerCallback.getMessageSync();
		Assert.assertTrue(fault.isFault());
		SCTest.verifyError((SCMPFault) fault, SCMPError.HV_WRONG_MAX_SESSIONS, " [IntValue 0 too low]", SCMPMsgType.REGISTER_SERVER);

		// maxConnections 0 value
		registerServerCall.setPortNumber(9100);
		registerServerCall.setMaxSessions(10);
		registerServerCall.setMaxConnections(0);
		registerServerCall.setImmediateConnect(true);
		registerServerCall.setKeepAliveInterval(360);
		registerServerCall.invoke(this.registerCallback, 1000);
		fault = this.registerCallback.getMessageSync();
		Assert.assertTrue(fault.isFault());
		SCTest.verifyError((SCMPFault) fault, SCMPError.HV_WRONG_MAX_CONNECTIONS, " [IntValue 0 too low]",
				SCMPMsgType.REGISTER_SERVER);

		// port too high 10000
		registerServerCall.setMaxSessions(10);
		registerServerCall.setMaxConnections(10);
		registerServerCall.setPortNumber(910000);
		registerServerCall.setImmediateConnect(true);
		registerServerCall.setKeepAliveInterval(360);
		registerServerCall.invoke(this.registerCallback, 1000);
		fault = this.registerCallback.getMessageSync();
		Assert.assertTrue(fault.isFault());
		SCTest.verifyError((SCMPFault) fault, SCMPError.HV_WRONG_PORTNR, " [IntValue 910000 not within limits]",
				SCMPMsgType.REGISTER_SERVER);
	}

	@Test
	public void registerServerCall() throws Exception {
		CommunicatorConfig config = new CommunicatorConfig("RegisterServerCallTester", TestConstants.HOST, TestConstants.PORT_TCP,
				"netty.tcp", 1000, 60, 10);
		RequesterContext context = new TestContext(config, this.msgId);
		IRequester req = new SCRequester(context);

		SCMPRegisterServerCall registerServerCall = (SCMPRegisterServerCall) SCMPCallFactory.REGISTER_SERVER_CALL.newInstance(req,
				"local-publish-service");

		registerServerCall.setMaxSessions(10);
		registerServerCall.setMaxConnections(10);
		registerServerCall.setPortNumber(51000);
		registerServerCall.setImmediateConnect(true);
		registerServerCall.setKeepAliveInterval(360);

		registerServerCall.invoke(this.registerCallback, 1000);
		SCTest.checkReply(this.registerCallback.getMessageSync());
		/*************** scmp inspect ********/
		SCMPInspectCall inspectCall = (SCMPInspectCall) SCMPCallFactory.INSPECT_CALL.newInstance(req);
		inspectCall.invoke(this.registerCallback, 3000);
		SCMPMessage inspect = this.registerCallback.getMessageSync();

		/*********************************** Verify registry entries in SC ********************************/
		String inspectMsg = (String) inspect.getBody();
		Map<String, String> inspectMap = SCTest.convertInspectStringToMap(inspectMsg);

		// TODO this will not work if configuration is changed
		String expectedScEntry = "file-service:file-service:ENABLED:file|local-publish-service:0 - local-publish-service_localhost/:51000 : 1|local-session-service:0 - local-session-service_localhost/:30000 : 10|";
		String scEntry = inspectMap.get("serviceRegistry");
		SCTest.assertEqualsUnorderedStringIgnorePorts(expectedScEntry, scEntry);

		expectedScEntry = "publish_localhost/:publish_localhost/:51000 : 10|fileServer:fileServer:80|local-session-service_localhost/:local-session-service_localhost/:30000 : 10|local-publish-service_localhost/:local-publish-service_localhost/:51000 : 1|";
		scEntry = (String) inspectMap.get("serverRegistry");
		SCTest.assertEqualsUnorderedStringIgnorePorts(expectedScEntry, scEntry);

		SCMPDeRegisterServerCall deRegisterServerCall = (SCMPDeRegisterServerCall) SCMPCallFactory.DEREGISTER_SERVER_CALL
				.newInstance(req, "local-publish-service");
		deRegisterServerCall.invoke(this.registerCallback, 1000);
		SCTest.checkReply(this.registerCallback.getMessageSync());

		/*********************************** Verify registry entries in SC ********************************/
		inspectCall = (SCMPInspectCall) SCMPCallFactory.INSPECT_CALL.newInstance(req);
		inspectCall.invoke(this.registerCallback, 1000);
		inspect = this.registerCallback.getMessageSync();
		inspectMsg = (String) inspect.getBody();
		inspectMap = SCTest.convertInspectStringToMap(inspectMsg);
		expectedScEntry = "local-session-service_localhost/:local-session-service_localhost/:30000 : 10|local-publish-service_localhost/:local-publish-service_localhost/:51000 : 1|fileServer:fileServer:80|";
		scEntry = (String) inspectMap.get("serverRegistry");
		SCTest.assertEqualsUnorderedStringIgnorePorts(expectedScEntry, scEntry);
	}

	protected class TestRegisterServerCallback extends SynchronousCallback {
	}
}
