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
import com.stabilit.scm.common.conf.RequeserConfig;
import com.stabilit.scm.common.net.SCMPCommunicationException;
import com.stabilit.scm.common.net.req.IRequester;
import com.stabilit.scm.common.net.req.RequesterFactory;
import com.stabilit.scm.common.scmp.SCMPError;
import com.stabilit.scm.common.scmp.SCMPHeaderAttributeKey;
import com.stabilit.scm.sc.ServiceConnector;
import com.stabilit.scm.sim.Simulation;
import com.stabilit.scm.unit.UnitCommandFactory;
import com.stabilit.scm.unit.test.SCTest;
import com.stabilit.scm.unit.test.SetupTestCases;
import com.stabilit.scm.unit.test.session.SuperSessionRegisterTestCase;

/**
 * Test case is not working at this time. Keep Alive is not implemented yet - means nothing realizes that server to
 * service is down. After registration of service only deregistration uses the server data communication works with
 * SC client to service.
 * 
 * @author JTraber
 */
public class WorseSCServerToServiceTestCase extends SuperSessionRegisterTestCase {

	/**
	 * The Constructor.
	 * 
	 * @param fileName
	 *            the file name
	 */
	public WorseSCServerToServiceTestCase(String fileName) {
		super(fileName);
	}

	@Before
	@Override
	public void setup() {
		try {
			SetupTestCases.init();
			CommandFactory.setCurrentCommandFactory(new UnitCommandFactory());
			ServiceConnector.main(null);
			Simulation.main(null);
			config = new RequeserConfig();
			config.load(fileName);
			RequesterFactory clientFactory = new RequesterFactory();
			client = clientFactory.newInstance(config.getClientConfig());
			client.connect(); // physical connect
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
	public void clnDataSCServerToServiceDisconnect() throws Exception {
		tearDownSCServerToService();

		// data call should fail because connection lost to simulation server
		SCMPClnDataCall clnDataCall = (SCMPClnDataCall) SCMPCallFactory.CLN_DATA_CALL.newInstance(client, this.scmpSession);
		clnDataCall.setServiceName("simulation");
		clnDataCall.setMessagInfo("asdasd");
		clnDataCall.setRequestBody("hello");
		try {
			clnDataCall.invoke();
		} catch (SCMPCommunicationException ex) {
			SCTest.verifyError((String) ex.getAttribute(SCMPHeaderAttributeKey.SC_ERROR_TEXT.getName()),
					(String) ex.getAttribute(SCMPHeaderAttributeKey.SC_ERROR_CODE.getName()),
					SCMPError.CONNECTION_LOST);
		}
		tearDownSCServerToClient();
	}

	private void tearDownSCServerToService() throws Exception {
		RequeserConfig config = new RequeserConfig();
		config.load("sc-sim.properties");
		RequesterFactory clientFactory = new RequesterFactory();
		IRequester tearDownClient = clientFactory.newInstance(config.getClientConfig());
		tearDownClient.connect(); // physical connect

		// disconnects server on SC to SimulatonServer
		SCMPSrvSystemCall systemCall = (SCMPSrvSystemCall) SCMPCallFactory.SRV_SYSTEM_CALL.newInstance(tearDownClient);
		systemCall.setRequestBody("simulation:P01_RTXS_RPRWS1");
		systemCall.invoke();
		tearDownClient.disconnect();
	}

	private void tearDownSCServerToClient() throws Exception {
		// disconnects SC Server to Client after sending response
		SCMPSrvSystemCall systemCall = (SCMPSrvSystemCall) SCMPCallFactory.SRV_SYSTEM_CALL.newInstance(client);
		systemCall.invoke();
	}
	
	@After
	@Override
	public void tearDown() throws Exception {
		client.disconnect();
		client.destroy();
	}
}