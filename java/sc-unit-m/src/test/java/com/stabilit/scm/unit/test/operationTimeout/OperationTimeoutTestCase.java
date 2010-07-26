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
package com.stabilit.scm.unit.test.operationTimeout;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.stabilit.scm.common.call.SCMPCallFactory;
import com.stabilit.scm.common.call.SCMPClnCreateSessionCall;
import com.stabilit.scm.common.conf.Constants;
import com.stabilit.scm.common.conf.RequesterConfigPool;
import com.stabilit.scm.common.net.req.IConnectionPool;
import com.stabilit.scm.common.net.req.Requester;
import com.stabilit.scm.common.scmp.ISCMPSynchronousCallback;
import com.stabilit.scm.common.scmp.SCMPError;
import com.stabilit.scm.common.scmp.SCMPFault;
import com.stabilit.scm.common.scmp.SCMPMessage;
import com.stabilit.scm.common.scmp.SCMPMsgType;
import com.stabilit.scm.common.util.SynchronousCallback;
import com.stabilit.scm.unit.TestContext;
import com.stabilit.scm.unit.test.SCTest;
import com.stabilit.scm.unit.test.SetupTestCases;
import com.stabilit.scm.unit.test.attach.SuperAttachTestCase;

public class OperationTimeoutTestCase extends SuperAttachTestCase {

	public OperationTimeoutTestCase(String fileName) {
		super(fileName);
		// Operation timeout in SC 2 second
		Constants.setOperationTimeoutMillis(2000);
	}

	@Override
	@Before
	public void setup() throws Exception {
		SetupTestCases.setupSCSessionServer1Connections();
		try {
			this.config = new RequesterConfigPool();
			this.config.load(fileName);
			this.testContext = new TestContext(this.config.getRequesterConfig());
			req = new Requester(this.testContext);
			IConnectionPool cp = this.testContext.getConnectionPool();
			cp.setCloseOnFree(true);
			cp.setMinConnections(5);
			cp.initMinConnections();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		clnAttachBefore();
	}

	// @Test
	public void callbackOperationTimedOutOnClientSynchronCommunicationLateCCRFreeConnection() throws Exception {
		// sets up a create session call
		SCMPClnCreateSessionCall createSessionCall = (SCMPClnCreateSessionCall) SCMPCallFactory.CLN_CREATE_SESSION_CALL
				.newInstance(req, "simulation");
		createSessionCall.setSessionInfo("sessionInfo");
		createSessionCall.setEchoInterval(300);
		createSessionCall.setEchoTimeout(10);

		// time to wait in create session on server must be lower than SC operation timeout, 1 second
		createSessionCall.setRequestBody("wait:" + 500);
		createSessionCall.invoke(this.attachCallback);

		// timeout on callback must be the lowest value, 1/2 seconds
		SCMPFault fault = (SCMPFault) this.attachCallback.getMessageSync(200);
		Assert.assertEquals("time for receiving message run out. Getting message synchronous failed.", fault.getCause()
				.getMessage());
		// wait for CCS to be received to late but initiates freeing the connection
		Thread.sleep(2000);
		// verify all connections freed properly
		IConnectionPool pool = this.testContext.getConnectionPool();
		int busyConnections = pool.getBusyConnections();
		Assert.assertEquals(0, busyConnections);

	}

	// @Test
	public void callbackOperationTimedOutOnClientSynchronCommunicationIdleTimeoutFreeConnection() throws Exception {
		// sets up a create session call
		SCMPClnCreateSessionCall createSessionCall = (SCMPClnCreateSessionCall) SCMPCallFactory.CLN_CREATE_SESSION_CALL
				.newInstance(req, "simulation");
		createSessionCall.setSessionInfo("sessionInfo");
		createSessionCall.setEchoInterval(300);
		createSessionCall.setEchoTimeout(10);

		// time to wait in create session on server must be higher than SC operation timeout
		createSessionCall.setRequestBody("wait:" + 4000);
		createSessionCall.invoke(this.attachCallback);

		// time to wait on client must be lower than operation timeout on SC & waiting time on server
		SCMPFault responseMessage = (SCMPFault) this.attachCallback.getMessageSync(500);
		Assert.assertTrue(responseMessage.isFault());
		Assert.assertEquals("time for receiving message run out. Getting message synchronous failed.", responseMessage
				.getCause().getMessage());
		// wait for idle connection to be received to initiates freeing the connection
		Thread.sleep(3000);
		// verify all connections freed properly
		IConnectionPool pool = this.testContext.getConnectionPool();
		int busyConnections = pool.getBusyConnections();
		Assert.assertEquals(0, busyConnections);
	}

	@Test
	public void idleOperationTimedOutOnClientSynchronCommunicationIdleTimeoutFreeConnection() throws Exception {
		for (int i = 0; i < 1; i++) {
			
		
		// sets up a create session call
		SCMPClnCreateSessionCall createSessionCall = (SCMPClnCreateSessionCall) SCMPCallFactory.CLN_CREATE_SESSION_CALL
				.newInstance(req, "simulation");
		createSessionCall.setSessionInfo("sessionInfo");
		createSessionCall.setEchoInterval(300);
		createSessionCall.setEchoTimeout(10);

		// time to wait in create session on server must be higher than SC operation timeout
		createSessionCall.setRequestBody("wait:" + 4000);
		ISCMPSynchronousCallback callback = new SynchronousCallback() {
		};
		createSessionCall.invoke(callback);

		// time to wait on client is default operation timeout & runs out first
		SCMPMessage msg = callback.getMessageSync();

		System.out.println("test callback: " + callback.toString());
		System.out.println( i + " laf *********** count = :" + msg.getBody());
		SCMPFault responseMessage = (SCMPFault) msg;
		Assert.assertTrue(responseMessage.isFault());
		if (responseMessage.getCause() == null) {
			SCTest.verifyError(responseMessage, SCMPError.OPERATION_TIMEOUT, SCMPMsgType.CLN_CREATE_SESSION);
		} else {
			Assert.assertEquals("operation timeout. operation - could not be completed.", responseMessage.getCause()
					.getMessage());
		}
//		// wait for freeing the connection not necessary - connection already freed - verify
//		IConnectionPool pool = this.testContext.getConnectionPool();
//		int busyConnections = pool.getBusyConnections();
//		Assert.assertEquals(0, busyConnections);
		}
	}
}
