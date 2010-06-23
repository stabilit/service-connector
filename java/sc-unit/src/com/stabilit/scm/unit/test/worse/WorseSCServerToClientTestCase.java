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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.stabilit.scm.cln.call.SCMPCallFactory;
import com.stabilit.scm.cln.call.SCMPClnDataCall;
import com.stabilit.scm.cln.call.SCMPSrvSystemCall;
import com.stabilit.scm.common.cmd.factory.CommandFactory;
import com.stabilit.scm.common.conf.RequesterConfigPool;
import com.stabilit.scm.common.net.req.IRequester;
import com.stabilit.scm.common.net.req.Requester;
import com.stabilit.scm.common.scmp.HasFaultResponseException;
import com.stabilit.scm.common.scmp.SCMPError;
import com.stabilit.scm.common.scmp.SCMPHeaderAttributeKey;
import com.stabilit.scm.sc.SC;
import com.stabilit.scm.srv.rr.Old_SessionServer;
import com.stabilit.scm.unit.UnitCommandFactory;
import com.stabilit.scm.unit.test.SCTest;
import com.stabilit.scm.unit.test.SetupTestCases;
import com.stabilit.scm.unit.test.session.SuperSessionRegisterTestCase;

/**
 * @author JTraber
 */
public class WorseSCServerToClientTestCase extends SuperSessionRegisterTestCase {

	/**
	 * The Constructor.
	 * 
	 * @param fileName
	 *            the file name
	 */
	public WorseSCServerToClientTestCase(String fileName) {
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
			config = new RequesterConfigPool();
			config.load(fileName);
			this.req = new Requester();
			req.setRequesterConfig(config.getRequesterConfig());
			req.connect(); // physical connect
			clnAttachBefore();
			registerServiceBefore();
			clnCreateSessionBefore();
			// needs to get disconnected (remove entry in connection registry before killing connection)
			clnDetachAfter();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void clnDataSCServerToClientDisconnect() throws Exception {
		// disconnects simulation server from SC after sending response
		SCMPSrvSystemCall systemCall = (SCMPSrvSystemCall) SCMPCallFactory.SRV_SYSTEM_CALL.newInstance(req);
		systemCall.invoke();

		// data call should fail because connection lost to simulation server
		SCMPClnDataCall clnDataCall = (SCMPClnDataCall) SCMPCallFactory.CLN_DATA_CALL.newInstance(req, this.scSession
				.getServiceName(), this.scSession.getSessionId());
		clnDataCall.setMessagInfo("asdasd");
		clnDataCall.setRequestBody("hello");
		try {
			clnDataCall.invoke();
		} catch (HasFaultResponseException ex) {
			SCTest.verifyError((String) ex.getAttribute(SCMPHeaderAttributeKey.SC_ERROR_TEXT.getName()), (String) ex
					.getAttribute(SCMPHeaderAttributeKey.SC_ERROR_CODE.getName()), SCMPError.CONNECTION_LOST);
		}
		tearDownSCServerToService();
	}

	private void tearDownSCServerToService() throws Exception {
		RequesterConfigPool config = new RequesterConfigPool();
		config.load("session-server.properties");
		IRequester tearDownClient = new Requester();
		tearDownClient.setRequesterConfig(config.getRequesterConfig());
		tearDownClient.connect(); // physical connect

		// disconnects server on SC to SimulatonServer
		SCMPSrvSystemCall systemCall = (SCMPSrvSystemCall) SCMPCallFactory.SRV_SYSTEM_CALL.newInstance(tearDownClient);
		systemCall.setRequestBody("simulation:P01_RTXS_RPRWS1");
		systemCall.invoke();
		tearDownClient.disconnect();
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
		req.disconnect();
	}
}