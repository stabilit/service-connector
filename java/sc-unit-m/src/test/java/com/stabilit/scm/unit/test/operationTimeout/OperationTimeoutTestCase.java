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

import org.junit.Test;

import com.stabilit.scm.common.call.SCMPCallFactory;
import com.stabilit.scm.common.call.SCMPClnCreateSessionCall;
import com.stabilit.scm.common.conf.Constants;
import com.stabilit.scm.common.net.req.IConnectionPool;
import com.stabilit.scm.common.scmp.SCMPFault;
import com.stabilit.scm.unit.test.attach.SuperAttachTestCase;

public class OperationTimeoutTestCase extends SuperAttachTestCase {

	public OperationTimeoutTestCase(String fileName) {
		super(fileName);
		// Operation timeout in SC 5 second
		Constants.setIdleTimeoutMillis(3000);
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
		createSessionCall.setRequestBody("wait:" + 1000);
		createSessionCall.invoke(this.attachCallback);

		// timeout on callback must be the lowest value, 1/2 seconds
		SCMPFault fault = (SCMPFault) this.attachCallback.getMessageSync(500);
		Assert.assertEquals("time for receiving message run out. Getting message synchronous failed.", fault.getCause()
				.getMessage());
		// wait for CCS to be received to late but initiates freeing the connection
		Thread.sleep(2000);
		// verify all connections freed properly
		IConnectionPool pool = this.testContext.getConnectionPool();
		int busyConnections = pool.getBusyConnections();
		Assert.assertEquals(0, busyConnections);

	}

	@Test
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
		Assert.assertEquals(responseMessage.getCause().getMessage(),
				"time for receiving message run out. Getting message synchronous failed.");
	}
}
