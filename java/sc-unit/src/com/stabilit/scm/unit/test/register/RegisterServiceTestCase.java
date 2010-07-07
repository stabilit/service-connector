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

import junit.framework.Assert;

import org.junit.Test;

import com.stabilit.scm.cln.call.SCMPCallException;
import com.stabilit.scm.cln.call.SCMPCallFactory;
import com.stabilit.scm.cln.call.SCMPDeRegisterServiceCall;
import com.stabilit.scm.cln.call.SCMPInspectCall;
import com.stabilit.scm.cln.call.SCMPRegisterServiceCall;
import com.stabilit.scm.common.conf.CommunicatorConfig;
import com.stabilit.scm.common.conf.ICommunicatorConfig;
import com.stabilit.scm.common.ctx.IContext;
import com.stabilit.scm.common.msg.impl.InspectMessage;
import com.stabilit.scm.common.net.req.IRequester;
import com.stabilit.scm.common.net.req.Requester;
import com.stabilit.scm.common.scmp.SCMPError;
import com.stabilit.scm.common.scmp.SCMPFault;
import com.stabilit.scm.common.scmp.SCMPHeaderAttributeKey;
import com.stabilit.scm.common.scmp.SCMPMessage;
import com.stabilit.scm.common.scmp.SCMPMsgType;
import com.stabilit.scm.unit.TestContext;
import com.stabilit.scm.unit.test.SCTest;
import com.stabilit.scm.unit.test.SuperTestCase;

public class RegisterServiceTestCase extends SuperTestCase {

	/**
	 * The Constructor.
	 * 
	 * @param fileName
	 *            the file name
	 */
	public RegisterServiceTestCase(String fileName) {
		super(fileName);
	}

	@Test
	public void failRegisterServiceCall() throws Exception {
		SCMPRegisterServiceCall registerServiceCall = (SCMPRegisterServiceCall) SCMPCallFactory.REGISTER_SERVICE_CALL
				.newInstance(req, "simulation2");

		/*********************** maxSessions 0 value *******************/
		registerServiceCall.setPortNumber(9100);
		registerServiceCall.setMaxSessions(0);
		registerServiceCall.setImmediateConnect(true);
		registerServiceCall.setKeepAliveTimeout(30);
		registerServiceCall.setKeepAliveInterval(360);

		try {
			registerServiceCall.invoke();
			Assert.fail("Should throw Exception!");
		} catch (SCMPCallException ex) {
			SCMPFault scmpFault = ex.getFault();
			Assert.assertEquals("1", scmpFault.getHeader(SCMPHeaderAttributeKey.MESSAGE_ID));
			SCTest.verifyError(ex.getFault(), SCMPError.VALIDATION_ERROR, SCMPMsgType.REGISTER_SERVICE);
		}
		/*********************** port too high 10000 *******************/
		registerServiceCall.setMaxSessions(10);
		registerServiceCall.setPortNumber(910000);
		registerServiceCall.setImmediateConnect(true);
		registerServiceCall.setKeepAliveTimeout(30);
		registerServiceCall.setKeepAliveInterval(360);

		try {
			registerServiceCall.invoke();
			Assert.fail("Should throw Exception!");
		} catch (SCMPCallException ex) {
			SCMPFault scmpFault = ex.getFault();
			Assert.assertEquals("2", scmpFault.getHeader(SCMPHeaderAttributeKey.MESSAGE_ID));
			SCTest.verifyError(ex.getFault(), SCMPError.VALIDATION_ERROR, SCMPMsgType.REGISTER_SERVICE);
		}
	}

	@Test
	public void registerServiceCall() throws Exception {
		ICommunicatorConfig config = new CommunicatorConfig("RegisterServiceCallTester", "localhost", 9000,
				"netty.tcp", 16, 1000, 60 ,10);
		IContext context = new TestContext(config);
		IRequester req = new Requester(context);

		SCMPRegisterServiceCall registerServiceCall = (SCMPRegisterServiceCall) SCMPCallFactory.REGISTER_SERVICE_CALL
				.newInstance(req, "publish-simulation");

		registerServiceCall.setMaxSessions(10);
		registerServiceCall.setPortNumber(7000);
		registerServiceCall.setImmediateConnect(true);
		registerServiceCall.setKeepAliveTimeout(30);
		registerServiceCall.setKeepAliveInterval(360);

		registerServiceCall.invoke();
		/*************** scmp inspect ********/
		SCMPInspectCall inspectCall = (SCMPInspectCall) SCMPCallFactory.INSPECT_CALL.newInstance(req);
		SCMPMessage inspect = inspectCall.invoke();

		/*********************************** Verify registry entries in SC ********************************/
		InspectMessage inspectMsg = (InspectMessage) inspect.getBody();
		String expectedScEntry = "P01_logging:0|publish-simulation:0 - publish-simulation_localhost/127.0.0.1: : 7000 : 10|P01_RTXS_sc1:0|simulation:0 - simulation_localhost/127.0.0.1: : 7000 : 1|P01_BCST_CH_sc1:0|";
		String scEntry = (String) inspectMsg.getAttribute("serviceRegistry");
		SCTest.assertEqualsUnorderedStringIgnorePorts(expectedScEntry, scEntry);
		Assert.assertEquals("2", inspect.getHeader(SCMPHeaderAttributeKey.MESSAGE_ID));

		expectedScEntry = "publish-simulation_localhost/127.0.0.1::publish-simulation_localhost/127.0.0.1: : 7000 : 10|simulation_localhost/127.0.0.1::simulation_localhost/127.0.0.1: : 7000 : 1|";
		scEntry = (String) inspectMsg.getAttribute("serverRegistry");
		SCTest.assertEqualsUnorderedStringIgnorePorts(expectedScEntry, scEntry);

		SCMPDeRegisterServiceCall deRegisterServiceCall = (SCMPDeRegisterServiceCall) SCMPCallFactory.DEREGISTER_SERVICE_CALL
				.newInstance(req, "publish-simulation");
		deRegisterServiceCall.invoke();

		/*********************************** Verify registry entries in SC ********************************/
		inspectCall = (SCMPInspectCall) SCMPCallFactory.INSPECT_CALL.newInstance(req);
		inspect = inspectCall.invoke();
		inspectMsg = (InspectMessage) inspect.getBody();
		expectedScEntry = "simulation_localhost/127.0.0.1::simulation_localhost/127.0.0.1: : 7000 : 1|";
		scEntry = (String) inspectMsg.getAttribute("serverRegistry");
		SCTest.assertEqualsUnorderedStringIgnorePorts(expectedScEntry, scEntry);
	}

	// TODO verify second registry!!
	public void secondRegisterServiceCall() throws Exception {
		SCMPRegisterServiceCall registerServiceCall = (SCMPRegisterServiceCall) SCMPCallFactory.REGISTER_SERVICE_CALL
				.newInstance(req, "");
		registerServiceCall.setMaxSessions(10);
		registerServiceCall.setPortNumber(9100);
		registerServiceCall.setImmediateConnect(true);
		registerServiceCall.setKeepAliveTimeout(30);
		registerServiceCall.setKeepAliveInterval(360);

		try {
			registerServiceCall.invoke();
			Assert.fail("Should throw Exception!");
		} catch (SCMPCallException e) {
			SCMPFault scmpFault = e.getFault();
			Assert.assertEquals("1", scmpFault.getHeader(SCMPHeaderAttributeKey.MESSAGE_ID));
			// TODO
			// SCTest.verifyError(e.getFault(), SCMPError.ALREADY_REGISTERED, SCMPMsgType.REGISTER_SERVICE);
		}

		SCMPDeRegisterServiceCall deRegisterServiceCall = (SCMPDeRegisterServiceCall) SCMPCallFactory.DEREGISTER_SERVICE_CALL
				.newInstance(req, "P01_RTXS_RPRWS1");
		deRegisterServiceCall.invoke();
	}
}
