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
package test.serviceconnector.register;

import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;
import org.serviceconnector.call.SCMPCallFactory;
import org.serviceconnector.call.SCMPDeRegisterServiceCall;
import org.serviceconnector.call.SCMPInspectCall;
import org.serviceconnector.call.SCMPRegisterServiceCall;
import org.serviceconnector.conf.CommunicatorConfig;
import org.serviceconnector.conf.ICommunicatorConfig;
import org.serviceconnector.net.req.IRequester;
import org.serviceconnector.net.req.IRequesterContext;
import org.serviceconnector.net.req.Requester;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPFault;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMsgType;
import org.serviceconnector.util.SynchronousCallback;

import test.serviceconnector.pool.TestContext;
import test.serviceconnector.unit.SCTest;
import test.serviceconnector.unit.SuperTestCase;


public class RegisterServiceTestCase extends SuperTestCase {

	protected RegisterServiceCallback registerCallback;

	/**
	 * The Constructor.
	 * 
	 * @param fileName
	 *            the file name
	 */
	public RegisterServiceTestCase(String fileName) {
		super(fileName);
		this.registerCallback = new RegisterServiceCallback();
	}

	@Test
	public void failRegisterUnknownService() throws Exception {
		SCMPRegisterServiceCall registerServiceCall = (SCMPRegisterServiceCall) SCMPCallFactory.REGISTER_SERVICE_CALL
				.newInstance(req, "notRegisteredServiceName");

		registerServiceCall.setMaxSessions(10);
		registerServiceCall.setMaxConnections(10);
		registerServiceCall.setPortNumber(7000);
		registerServiceCall.setImmediateConnect(true);
		registerServiceCall.setKeepAliveInterval(360);

		registerServiceCall.invoke(this.registerCallback, 1000);
		SCMPFault fault = (SCMPFault) this.registerCallback.getMessageSync();
		Assert.assertTrue(fault.isFault());
		SCTest.verifyError((SCMPFault) fault, SCMPError.NOT_FOUND, " [service not found for notRegisteredServiceName]", SCMPMsgType.REGISTER_SERVICE);
	}

	@Test
	public void failRegisterServiceCallWrongHeader() throws Exception {
		SCMPRegisterServiceCall registerServiceCall = (SCMPRegisterServiceCall) SCMPCallFactory.REGISTER_SERVICE_CALL
				.newInstance(req, "simulation2");

		// keep alive interval not set
		registerServiceCall.setMaxSessions(10);
		registerServiceCall.setMaxConnections(10);
		registerServiceCall.setPortNumber(9100);
		registerServiceCall.setImmediateConnect(true);
		registerServiceCall.invoke(this.registerCallback, 1000);
		SCMPMessage fault = this.registerCallback.getMessageSync();
		Assert.assertTrue(fault.isFault());
		SCTest.verifyError((SCMPFault) fault, SCMPError.HV_WRONG_KEEPALIVE_INTERVAL, " [IntValue must be set]",
				SCMPMsgType.REGISTER_SERVICE);

		// maxSessions 0 value
		registerServiceCall.setPortNumber(9100);
		registerServiceCall.setMaxSessions(0);
		registerServiceCall.setMaxConnections(10);
		registerServiceCall.setImmediateConnect(true);
		registerServiceCall.setKeepAliveInterval(360);
		registerServiceCall.invoke(this.registerCallback, 1000);
		fault = this.registerCallback.getMessageSync();
		Assert.assertTrue(fault.isFault());
		SCTest.verifyError((SCMPFault) fault, SCMPError.HV_WRONG_MAX_SESSIONS, " [IntValue 0 too low]",
				SCMPMsgType.REGISTER_SERVICE);

		// maxConnections 0 value
		registerServiceCall.setPortNumber(9100);
		registerServiceCall.setMaxSessions(10);
		registerServiceCall.setMaxConnections(0);
		registerServiceCall.setImmediateConnect(true);
		registerServiceCall.setKeepAliveInterval(360);
		registerServiceCall.invoke(this.registerCallback, 1000);
		fault = this.registerCallback.getMessageSync();
		Assert.assertTrue(fault.isFault());
		SCTest.verifyError((SCMPFault) fault, SCMPError.HV_WRONG_MAX_CONNECTIONS, " [IntValue 0 too low]",
				SCMPMsgType.REGISTER_SERVICE);

		// port too high 10000
		registerServiceCall.setMaxSessions(10);
		registerServiceCall.setMaxConnections(10);
		registerServiceCall.setPortNumber(910000);
		registerServiceCall.setImmediateConnect(true);
		registerServiceCall.setKeepAliveInterval(360);
		registerServiceCall.invoke(this.registerCallback, 1000);
		fault = this.registerCallback.getMessageSync();
		Assert.assertTrue(fault.isFault());
		SCTest.verifyError((SCMPFault) fault, SCMPError.HV_WRONG_PORTNR, " [IntValue 910000 not within limits]",
				SCMPMsgType.REGISTER_SERVICE);
	}

	@Test
	public void registerServiceCall() throws Exception {
		ICommunicatorConfig config = new CommunicatorConfig("RegisterServiceCallTester", "localhost", 9000,
				"netty.tcp", 1000, 60, 10);
		IRequesterContext context = new TestContext(config, this.msgId);
		IRequester req = new Requester(context);

		SCMPRegisterServiceCall registerServiceCall = (SCMPRegisterServiceCall) SCMPCallFactory.REGISTER_SERVICE_CALL
				.newInstance(req, "publish-simulation");

		registerServiceCall.setMaxSessions(10);
		registerServiceCall.setMaxConnections(10);
		registerServiceCall.setPortNumber(7000);
		registerServiceCall.setImmediateConnect(true);
		registerServiceCall.setKeepAliveInterval(360);

		registerServiceCall.invoke(this.registerCallback, 1000);
		this.registerCallback.getMessageSync();
		/*************** scmp inspect ********/
		SCMPInspectCall inspectCall = (SCMPInspectCall) SCMPCallFactory.INSPECT_CALL.newInstance(req);
		inspectCall.invoke(this.registerCallback, 3);
		SCMPMessage inspect = this.registerCallback.getMessageSync();

		/*********************************** Verify registry entries in SC ********************************/
		String inspectMsg = (String) inspect.getBody();
		Map<String, String> inspectMap = SCTest.convertInspectStringToMap(inspectMsg);

		String expectedScEntry = "P01_logging:0|publish-simulation:0 - publish-simulation_localhost/:7000 : 10|P01_RTXS_sc1:0|simulation:0 - simulation_localhost/:7000 : 10|P01_BCST_CH_sc1:0|";
		String scEntry = inspectMap.get("serviceRegistry");
		SCTest.assertEqualsUnorderedStringIgnorePorts(expectedScEntry, scEntry);

		expectedScEntry = "publish-simulation_localhost/:publish-simulation_localhost/:7000 : 10|simulation_localhost/:simulation_localhost/:7000 : 10|";
		scEntry = (String) inspectMap.get("serverRegistry");
		SCTest.assertEqualsUnorderedStringIgnorePorts(expectedScEntry, scEntry);

		SCMPDeRegisterServiceCall deRegisterServiceCall = (SCMPDeRegisterServiceCall) SCMPCallFactory.DEREGISTER_SERVICE_CALL
				.newInstance(req, "publish-simulation");
		deRegisterServiceCall.invoke(this.registerCallback, 1000);
		this.registerCallback.getMessageSync();

		/*********************************** Verify registry entries in SC ********************************/
		inspectCall = (SCMPInspectCall) SCMPCallFactory.INSPECT_CALL.newInstance(req);
		inspectCall.invoke(this.registerCallback, 1000);
		inspect = this.registerCallback.getMessageSync();
		inspectMsg = (String) inspect.getBody();
		inspectMap = SCTest.convertInspectStringToMap(inspectMsg);
		expectedScEntry = "simulation_localhost/:simulation_localhost/:7000 : 10|";
		scEntry = (String) inspectMap.get("serverRegistry");
		SCTest.assertEqualsUnorderedStringIgnorePorts(expectedScEntry, scEntry);
	}

	@Test
	public void secondRegisterServiceCall() throws Exception {
		ICommunicatorConfig config = new CommunicatorConfig("RegisterServiceCallTester", "localhost", 9000,
				"netty.tcp", 1, 60, 10);
		IRequesterContext context = new TestContext(config, this.msgId);
		IRequester req = new Requester(context);
		SCMPRegisterServiceCall registerServiceCall = (SCMPRegisterServiceCall) SCMPCallFactory.REGISTER_SERVICE_CALL
				.newInstance(req, "publish-simulation");

		registerServiceCall.setMaxSessions(10);
		registerServiceCall.setMaxConnections(10);
		registerServiceCall.setPortNumber(7000);
		registerServiceCall.setImmediateConnect(true);
		registerServiceCall.setKeepAliveInterval(360);
		registerServiceCall.invoke(this.registerCallback, 1000);
		this.registerCallback.getMessageSync();

		registerServiceCall = (SCMPRegisterServiceCall) SCMPCallFactory.REGISTER_SERVICE_CALL.newInstance(req,
				"publish-simulation");
		registerServiceCall.setMaxSessions(10);
		registerServiceCall.setMaxConnections(10);
		registerServiceCall.setPortNumber(7000);
		registerServiceCall.setImmediateConnect(true);
		registerServiceCall.setKeepAliveInterval(360);

		registerServiceCall.invoke(this.registerCallback, 1000);
		SCMPFault message = (SCMPFault) this.registerCallback.getMessageSync();
		Assert
				.assertEquals(SCMPMsgType.REGISTER_SERVICE.getValue(), message
						.getHeader(SCMPHeaderAttributeKey.MSG_TYPE));
		Assert.assertEquals(SCMPError.SERVER_ALREADY_REGISTERED.getErrorCode(), message
				.getHeader(SCMPHeaderAttributeKey.SC_ERROR_CODE));

		SCMPDeRegisterServiceCall deRegisterServiceCall = (SCMPDeRegisterServiceCall) SCMPCallFactory.DEREGISTER_SERVICE_CALL
				.newInstance(req, "publish-simulation");
		deRegisterServiceCall.invoke(this.registerCallback, 3000);
		this.registerCallback.getMessageSync();
	}

	protected class RegisterServiceCallback extends SynchronousCallback {
	}
}
