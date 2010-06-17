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
package com.stabilit.scm.unit.test.worse;

import java.util.Date;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.stabilit.scm.cln.call.SCMPCallException;
import com.stabilit.scm.cln.call.SCMPCallFactory;
import com.stabilit.scm.cln.call.SCMPClnDataCall;
import com.stabilit.scm.cln.call.SCMPClnDeleteSessionCall;
import com.stabilit.scm.cln.call.SCMPClnSystemCall;
import com.stabilit.scm.cln.call.SCMPInspectCall;
import com.stabilit.scm.common.cmd.factory.CommandFactory;
import com.stabilit.scm.common.conf.RequesterConfig;
import com.stabilit.scm.common.msg.impl.InspectMessage;
import com.stabilit.scm.common.net.req.Requester;
import com.stabilit.scm.common.scmp.SCMPError;
import com.stabilit.scm.common.scmp.SCMPMessage;
import com.stabilit.scm.common.scmp.SCMPMsgType;
import com.stabilit.scm.common.util.ValidatorUtility;
import com.stabilit.scm.sc.SC;
import com.stabilit.scm.srv.rr.Old_SessionServer;
import com.stabilit.scm.unit.UnitCommandFactory;
import com.stabilit.scm.unit.test.SCTest;
import com.stabilit.scm.unit.test.SetupTestCases;
import com.stabilit.scm.unit.test.session.SuperSessionRegisterTestCase;

/**
 * @author JTraber
 */
public class WorseScenarioSimulationServerTestCase extends SuperSessionRegisterTestCase {

	/**
	 * The Constructor.
	 * 
	 * @param fileName
	 *            the file name
	 */
	public WorseScenarioSimulationServerTestCase(String fileName) {
		super(fileName);
	}

	@Before
	@Override
	public void setup() {
		try {
			SetupTestCases.init();
			CommandFactory.setCurrentCommandFactory(new UnitCommandFactory());
			SC.main(null);
			Old_SessionServer.main(null);
			config = new RequesterConfig();
			config.load(fileName);
			this.req = new Requester();
			req.setRequesterConfig(config.getRequesterConfig());
			req.connect(); // physical connect
			clnAttachBefore();
			registerServiceBefore();
			clnCreateSessionBefore();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void clnDeleteSessionSimulationServerDisconnect() throws Exception {
		// disconnects simulation server from SC after sending response
		SCMPClnSystemCall systemCall = (SCMPClnSystemCall) SCMPCallFactory.CLN_SYSTEM_CALL.newInstance(req,
				this.scSession.getServiceName(), this.scSession.getSessionId());
		systemCall.setMaxNodes(2);
		systemCall.invoke();

		/*
		 * delete session shouldn't fail even service is down, clean up works fine client doesn't notice the failure
		 * TODO is right verify with jan
		 */
		SCMPClnDeleteSessionCall deleteSessionCall = (SCMPClnDeleteSessionCall) SCMPCallFactory.CLN_DELETE_SESSION_CALL
				.newInstance(req, this.scSession.getServiceName(), this.scSession.getSessionId());
		deleteSessionCall.invoke();

		SCMPInspectCall inspectCall = (SCMPInspectCall) SCMPCallFactory.INSPECT_CALL.newInstance(req);
		SCMPMessage inspect = inspectCall.invoke();
		/*********************************** Verify registry entries in SC ********************************/
		InspectMessage inspectMsg = (InspectMessage) inspect.getBody();
		Assert.assertEquals("", inspectMsg.getAttribute("sessionRegistry"));
		Date localDateTime = ValidatorUtility.validateLocalDateTime(localDateTimeOfConnect);
		String expectedScEntry = "localhost/127.0.0.1::localhost/127.0.0.1::SCMP [header={messageID=1, msgType=ATTACH, keepAliveInterval=360, scVersion=1.0-000, localDateTime="
				+ localDateTimeOfConnect
				+ ", keepAliveTimeout=30}] MapBean: localDateTime="
				+ localDateTime
				+ ";keepAliveTimeout=30,360;|";
		String scEntry = (String) inspectMsg.getAttribute("clientRegistry");
		// truncate /127.0.0.1:3640 because port may vary.
		scEntry = scEntry.replaceAll("/127.0.0.1:\\d*", "/127.0.0.1:");
		Assert.assertEquals(expectedScEntry, scEntry);

		expectedScEntry = "P01_RTXS_RPRWS1:0 - P01_RTXS_RPRWS1_localhost/127.0.0.1: : 9000 : 10|simulation:0 - simulation_localhost/127.0.0.1: : 7000 : 1|";
		scEntry = (String) inspectMsg.getAttribute("serviceRegistry");
		// truncate /127.0.0.1:3640 because port may vary.
		scEntry = scEntry.replaceAll("/127.0.0.1:\\d*", "/127.0.0.1:");
		Assert.assertEquals(expectedScEntry, scEntry);
	}

	@Test
	public void clnDataSimulationServerDisconnect() throws Exception {

		// disconnects simulation server from SC after sending response
		SCMPClnSystemCall systemCall = (SCMPClnSystemCall) SCMPCallFactory.CLN_SYSTEM_CALL.newInstance(req,
				this.scSession.getServiceName(), this.scSession.getSessionId());
		systemCall.setMaxNodes(2);
		systemCall.invoke();

		// data call should fail because connection lost to simulation server
		SCMPClnDataCall clnDataCall = (SCMPClnDataCall) SCMPCallFactory.CLN_DATA_CALL.newInstance(req, this.scSession
				.getServiceName(), this.scSession.getSessionId());
		clnDataCall.setMessagInfo("asdasd");
		clnDataCall.setRequestBody("hello");
		try {
			clnDataCall.invoke();
		} catch (SCMPCallException ex) {
			SCTest.verifyError(ex.getFault(), SCMPError.SERVER_ERROR, SCMPMsgType.CLN_DATA);
		}

		SCMPInspectCall inspectCall = (SCMPInspectCall) SCMPCallFactory.INSPECT_CALL.newInstance(req);
		SCMPMessage inspect = inspectCall.invoke();
		/*********************************** Verify registry entries in SC ********************************/
		InspectMessage inspectMsg = (InspectMessage) inspect.getBody();
		Assert.assertEquals("", inspectMsg.getAttribute("sessionRegistry"));
		Date localDateTime = ValidatorUtility.validateLocalDateTime(localDateTimeOfConnect);
		String expectedScEntry = "localhost/127.0.0.1::localhost/127.0.0.1::SCMP [header={messageID=1, msgType=ATTACH, keepAliveInterval=360, scVersion=1.0-000, localDateTime="
				+ localDateTimeOfConnect
				+ ", keepAliveTimeout=30}] MapBean: localDateTime="
				+ localDateTime
				+ ";keepAliveTimeout=30,360;|";
		String scEntry = (String) inspectMsg.getAttribute("clientRegistry");
		// truncate /127.0.0.1:3640 because port may vary.
		scEntry = scEntry.replaceAll("/127.0.0.1:\\d*", "/127.0.0.1:");
		Assert.assertEquals(expectedScEntry, scEntry);

		expectedScEntry = "P01_RTXS_RPRWS1:0 - P01_RTXS_RPRWS1_localhost/127.0.0.1: : 9000 : 10|simulation:0 - simulation_localhost/127.0.0.1: : 7000 : 1|";
		scEntry = (String) inspectMsg.getAttribute("serviceRegistry");
		// truncate /127.0.0.1:3640 because port may vary.
		scEntry = scEntry.replaceAll("/127.0.0.1:\\d*", "/127.0.0.1:");
		Assert.assertEquals(expectedScEntry, scEntry);
	}

	/**
	 * Tear down. Needs to be overridden because clnDeleteSession() from usual procedure is not possible this time -
	 * backend server already down.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@After
	@Override
	public void tearDown() throws Exception {
		this.deRegisterServiceAfter();
		this.clnDetachAfter();
		req.disconnect();
		req.destroy();
	}
}