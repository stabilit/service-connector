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
package com.stabilit.scm.unit.test.register;

import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;

import com.stabilit.scm.cln.call.SCMPCallException;
import com.stabilit.scm.common.call.SCMPCallFactory;
import com.stabilit.scm.common.call.SCMPDeRegisterServiceCall;
import com.stabilit.scm.common.call.SCMPInspectCall;
import com.stabilit.scm.common.call.SCMPRegisterServiceCall;
import com.stabilit.scm.common.conf.CommunicatorConfig;
import com.stabilit.scm.common.conf.ICommunicatorConfig;
import com.stabilit.scm.common.net.req.IRequester;
import com.stabilit.scm.common.net.req.IRequesterContext;
import com.stabilit.scm.common.net.req.Requester;
import com.stabilit.scm.common.scmp.SCMPError;
import com.stabilit.scm.common.scmp.SCMPFault;
import com.stabilit.scm.common.scmp.SCMPHeaderAttributeKey;
import com.stabilit.scm.common.scmp.SCMPMessage;
import com.stabilit.scm.common.scmp.SCMPMsgType;
import com.stabilit.scm.common.util.SynchronousCallback;
import com.stabilit.scm.unit.TestContext;
import com.stabilit.scm.unit.test.SCTest;
import com.stabilit.scm.unit.test.SuperTestCase;

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
	public void failRegisterServiceCall() throws Exception {
		SCMPRegisterServiceCall registerServiceCall = (SCMPRegisterServiceCall) SCMPCallFactory.REGISTER_SERVICE_CALL
				.newInstance(req, "simulation2");

		/*********************** maxSessions 0 value *******************/
		registerServiceCall.setPortNumber(9100);
		registerServiceCall.setMaxSessions(0);
		registerServiceCall.setImmediateConnect(true);
		registerServiceCall.setKeepAliveInterval(360);

		registerServiceCall.invoke(this.registerCallback);
		SCMPMessage fault = this.registerCallback.getMessageSync();

		Assert.assertTrue(fault.isFault());
		SCTest.verifyError((SCMPFault) fault, SCMPError.VALIDATION_ERROR, SCMPMsgType.REGISTER_SERVICE);
		/*********************** port too high 10000 *******************/
		registerServiceCall.setMaxSessions(10);
		registerServiceCall.setPortNumber(910000);
		registerServiceCall.setImmediateConnect(true);
		registerServiceCall.setKeepAliveInterval(360);

		registerServiceCall.invoke(this.registerCallback);
		fault = this.registerCallback.getMessageSync();

		Assert.assertTrue(fault.isFault());
		SCTest.verifyError((SCMPFault) fault, SCMPError.VALIDATION_ERROR, SCMPMsgType.REGISTER_SERVICE);

	}

	@Test
	public void registerServiceCall() throws Exception {
		ICommunicatorConfig config = new CommunicatorConfig("RegisterServiceCallTester", "localhost", 9000,
				"netty.tcp", 16, 1000, 60, 10);
		IRequesterContext context = new TestContext(config, this.msgId);
		IRequester req = new Requester(context);

		SCMPRegisterServiceCall registerServiceCall = (SCMPRegisterServiceCall) SCMPCallFactory.REGISTER_SERVICE_CALL
				.newInstance(req, "publish-simulation");

		registerServiceCall.setMaxSessions(10);
		registerServiceCall.setPortNumber(7000);
		registerServiceCall.setImmediateConnect(true);
		registerServiceCall.setKeepAliveInterval(360);

		registerServiceCall.invoke(this.registerCallback);
		this.registerCallback.getMessageSync();
		/*************** scmp inspect ********/
		SCMPInspectCall inspectCall = (SCMPInspectCall) SCMPCallFactory.INSPECT_CALL.newInstance(req);
		inspectCall.invoke(this.registerCallback);
		SCMPMessage inspect = this.registerCallback.getMessageSync();

		/*********************************** Verify registry entries in SC ********************************/
		String inspectMsg = (String) inspect.getBody();
		Map<String, String> inspectMap = SCTest.convertInspectStringToMap(inspectMsg);

		String expectedScEntry = "P01_logging:0|publish-simulation:0 - publish-simulation_localhost/127.0.0.1: : 7000 : 10|P01_RTXS_sc1:0|simulation:0 - simulation_localhost/127.0.0.1: : 7000 : 10|P01_BCST_CH_sc1:0|";
		String scEntry = inspectMap.get("serviceRegistry");
		SCTest.assertEqualsUnorderedStringIgnorePorts(expectedScEntry, scEntry);

		expectedScEntry = "publish-simulation_localhost/127.0.0.1::publish-simulation_localhost/127.0.0.1: : 7000 : 10|simulation_localhost/127.0.0.1::simulation_localhost/127.0.0.1: : 7000 : 10|";
		scEntry = (String) inspectMap.get("serverRegistry");
		SCTest.assertEqualsUnorderedStringIgnorePorts(expectedScEntry, scEntry);

		SCMPDeRegisterServiceCall deRegisterServiceCall = (SCMPDeRegisterServiceCall) SCMPCallFactory.DEREGISTER_SERVICE_CALL
				.newInstance(req, "publish-simulation");
		deRegisterServiceCall.invoke(this.registerCallback);
		this.registerCallback.getMessageSync();

		/*********************************** Verify registry entries in SC ********************************/
		inspectCall = (SCMPInspectCall) SCMPCallFactory.INSPECT_CALL.newInstance(req);
		inspectCall.invoke(this.registerCallback);
		inspect = this.registerCallback.getMessageSync();
		inspectMsg = (String) inspect.getBody();
		inspectMap = SCTest.convertInspectStringToMap(inspectMsg);
		expectedScEntry = "simulation_localhost/127.0.0.1::simulation_localhost/127.0.0.1: : 7000 : 10|";
		scEntry = (String) inspectMap.get("serverRegistry");
		SCTest.assertEqualsUnorderedStringIgnorePorts(expectedScEntry, scEntry);
	}

	// TODO verify second registry!!
	public void secondRegisterServiceCall() throws Exception {
		SCMPRegisterServiceCall registerServiceCall = (SCMPRegisterServiceCall) SCMPCallFactory.REGISTER_SERVICE_CALL
				.newInstance(req, "");
		registerServiceCall.setMaxSessions(10);
		registerServiceCall.setPortNumber(9100);
		registerServiceCall.setImmediateConnect(true);
		registerServiceCall.setKeepAliveInterval(360);

		try {
			registerServiceCall.invoke(this.registerCallback);
			this.registerCallback.getMessageSync();
			Assert.fail("Should throw Exception!");
		} catch (SCMPCallException e) {
			SCMPFault scmpFault = e.getFault();
			Assert.assertEquals("1", scmpFault.getHeader(SCMPHeaderAttributeKey.MESSAGE_ID));
			// TODO
			// SCTest.verifyError(e.getFault(), SCMPError.ALREADY_REGISTERED, SCMPMsgType.REGISTER_SERVICE);
		}

		SCMPDeRegisterServiceCall deRegisterServiceCall = (SCMPDeRegisterServiceCall) SCMPCallFactory.DEREGISTER_SERVICE_CALL
				.newInstance(req, "P01_RTXS_RPRWS1");
		deRegisterServiceCall.invoke(this.registerCallback);
		this.registerCallback.getMessageSync();
	}

	protected class RegisterServiceCallback extends SynchronousCallback {
	}
}
