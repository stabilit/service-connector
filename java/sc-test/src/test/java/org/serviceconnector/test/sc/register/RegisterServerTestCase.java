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

	protected RegisterServerCallback registerCallback;

	/**
	 * The Constructor.
	 * 
	 * @param fileName
	 *            the file name
	 */
	public RegisterServerTestCase(String fileName) {
		super(fileName);
		this.registerCallback = new RegisterServerCallback();
	}

	@Test
	public void failRegisterServerForUnknownService() throws Exception {
		SCMPRegisterServerCall registerServerCall = (SCMPRegisterServerCall) SCMPCallFactory.REGISTER_SERVER_CALL
				.newInstance(req, "notConfiguredServiceName");

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
		SCMPRegisterServerCall registerServerCall = (SCMPRegisterServerCall) SCMPCallFactory.REGISTER_SERVER_CALL
				.newInstance(req, "simulation2");

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
		SCTest.verifyError((SCMPFault) fault, SCMPError.HV_WRONG_MAX_SESSIONS, " [IntValue 0 too low]",
				SCMPMsgType.REGISTER_SERVER);

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

		SCMPRegisterServerCall registerServerCall = (SCMPRegisterServerCall) SCMPCallFactory.REGISTER_SERVER_CALL
				.newInstance(req, "publish-simulation");

		registerServerCall.setMaxSessions(10);
		registerServerCall.setMaxConnections(10);
		registerServerCall.setPortNumber(51000);
		registerServerCall.setImmediateConnect(true);
		registerServerCall.setKeepAliveInterval(360);

		registerServerCall.invoke(this.registerCallback, 1000);
		this.registerCallback.getMessageSync();
		/*************** scmp inspect ********/
		SCMPInspectCall inspectCall = (SCMPInspectCall) SCMPCallFactory.INSPECT_CALL.newInstance(req);
		inspectCall.invoke(this.registerCallback, 3000);
		SCMPMessage inspect = this.registerCallback.getMessageSync();

		/*********************************** Verify registry entries in SC ********************************/
		String inspectMsg = (String) inspect.getBody();
		Map<String, String> inspectMap = SCTest.convertInspectStringToMap(inspectMsg);

		String expectedScEntry = "P01_logging:0|publish-simulation:0 - publish-simulation_localhost/:51000 : 10 - publish-simulation_localhost/:51000 : 10|1conn:0 - 1conn_localhost/:41000 : 10|enableService:0|P01_RTXS_sc1:0|simulation:0 - simulation_localhost/:30000 : 10|P01_BCST_CH_sc1:0|";
		String scEntry = inspectMap.get("serviceRegistry");
		SCTest.assertEqualsUnorderedStringIgnorePorts(expectedScEntry, scEntry);

		expectedScEntry = "1conn_localhost/:1conn_localhost/:41000 : 10|publish-simulation_localhost/:publish-simulation_localhost/:51000 : 10|publish-simulation_localhost/:publish-simulation_localhost/:51000 : 10|simulation_localhost/:simulation_localhost/:30000 : 10|";
		scEntry = (String) inspectMap.get("serverRegistry");
		SCTest.assertEqualsUnorderedStringIgnorePorts(expectedScEntry, scEntry);

		SCMPDeRegisterServerCall deRegisterServerCall = (SCMPDeRegisterServerCall) SCMPCallFactory.DEREGISTER_SERVER_CALL
				.newInstance(req, "publish-simulation");
		deRegisterServerCall.invoke(this.registerCallback, 1000);
		SCTest.checkReply(this.registerCallback.getMessageSync());

		/*********************************** Verify registry entries in SC ********************************/
		inspectCall = (SCMPInspectCall) SCMPCallFactory.INSPECT_CALL.newInstance(req);
		inspectCall.invoke(this.registerCallback, 1000);
		inspect = this.registerCallback.getMessageSync();
		inspectMsg = (String) inspect.getBody();
		inspectMap = SCTest.convertInspectStringToMap(inspectMsg);
		expectedScEntry = "publish-simulation_localhost/:publish-simulation_localhost/:51000 : 10|publish-simulation_localhost/:publish-simulation_localhost/:51000 : 10|1conn_localhost/:1conn_localhost/:41000 : 10|simulation_localhost/:simulation_localhost/:30000 : 10|";
		scEntry = (String) inspectMap.get("serverRegistry");
		SCTest.assertEqualsUnorderedStringIgnorePorts(expectedScEntry, scEntry);
	}

	protected class RegisterServerCallback extends SynchronousCallback {
	}
}
